#!/usr/bin/env python3
"""
generate_frida_migration.py
----------------------------
Converts the Frida food composition xlsx into a Flyway SQL migration that
seeds the ingredients table.

Usage:
    python3 generate_frida_migration.py <path-to-Frida_x_x_Dataset.xlsx>

Output:
    V10__seed_frida_ingredients.sql  (written next to this script)

Column mapping (Data_Table sheet, 0-indexed):
    0   Danish name       (FødevareNavn)
    1   English name      (FoodName)
    2   FoodID
    5   Energy (kcal)                      kcal/100g
    9   Protein, labelling                 g/100g
   12   Available carbohydrate, labelling  g/100g
   14   Fat                                g/100g
   16   Salt labelling                     g/100g
   97   Sum sugars                         g/100g
  180   Sum saturated fatty acids          g/100g
"""

import sys
import uuid
import os
import openpyxl

COL_DANISH_NAME  = 0
COL_ENGLISH_NAME = 1
COL_FOOD_ID      = 2
COL_KCAL         = 5
COL_PROTEIN      = 9
COL_CARBS        = 12
COL_FAT          = 14
COL_SALT         = 16
COL_SUGARS       = 97
COL_SAT_FAT      = 180

BATCH_SIZE  = 100
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "V10__seed_frida_ingredients.sql")


def val(row, col):
    v = row[col] if col < len(row) else None
    if v is None:
        return "NULL"
    try:
        return str(round(float(v), 2))
    except (TypeError, ValueError):
        return "NULL"


def escape(s):
    return s.replace("'", "''") if s else ""


def generate(xlsx_path: str):
    print(f"Loading {xlsx_path} ...")
    wb = openpyxl.load_workbook(xlsx_path, read_only=True)
    ws = wb["Data_Table"]

    value_rows = []
    for row in ws.iter_rows(min_row=5, values_only=True):
        if row[COL_FOOD_ID] is None:
            continue
        english = row[COL_ENGLISH_NAME]
        danish  = row[COL_DANISH_NAME]
        name    = escape(english if english else danish)
        if not name:
            continue

        uid     = str(uuid.uuid4())
        kcal    = val(row, COL_KCAL)
        protein = val(row, COL_PROTEIN)
        carbs   = val(row, COL_CARBS)
        fat     = val(row, COL_FAT)
        salt    = val(row, COL_SALT)
        sugars  = val(row, COL_SUGARS)
        sat_fat = val(row, COL_SAT_FAT)

        value_rows.append(
            f"('{uid}', '{name}', {kcal}, {protein}, {carbs}, {fat}, {sat_fat}, {sugars}, {salt}, NULL)"
        )

    col_list = "(id, name, calories, protein, carbohydrates, fat, saturated_fat, sugars, salt, price)"

    lines = [
        "-- Frida food database seed (auto-generated - do not edit by hand)",
        "-- Re-generate with: python3 generate_frida_migration.py <xlsx>",
        "-- All nutritional values are per 100g/ml",
        "",
    ]

    # Batched INSERT ... VALUES - plain and simple, no derived tables
    for i in range(0, len(value_rows), BATCH_SIZE):
        chunk = value_rows[i:i + BATCH_SIZE]
        lines.append(f"INSERT IGNORE INTO ingredients {col_list}")
        lines.append("VALUES")
        for j, row_val in enumerate(chunk):
            comma = "," if j < len(chunk) - 1 else ";"
            lines.append(f"  {row_val}{comma}")
        lines.append("")

    sql = "\n".join(lines)

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write(sql)

    batch_count = (len(value_rows) + BATCH_SIZE - 1) // BATCH_SIZE
    print(f"Written {len(value_rows)} ingredients in {batch_count} batches -> {OUTPUT_FILE}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 generate_frida_migration.py <path-to-xlsx>")
        sys.exit(1)
    generate(sys.argv[1])
