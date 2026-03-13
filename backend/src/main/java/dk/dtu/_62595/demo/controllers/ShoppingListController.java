package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.AddShoppingListItemRequest;
import dk.dtu._62595.demo.dto.ShoppingListItemDto;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.services.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AuthController authController;

    public ShoppingListController(ShoppingListService shoppingListService,
                                  AuthController authController) {
        this.shoppingListService = shoppingListService;
        this.authController = authController;
    }

    // ── Shopping list endpoints ───────────────────────────────────────────────

    @GetMapping("/{groupId}")
    public ResponseEntity<List<ShoppingListItemDto>> getItems(@PathVariable UUID groupId) {
        User currentUser = authController.getLoggedInUser();
        return ResponseEntity.ok(shoppingListService.getItemsForGroup(groupId, currentUser));
    }

    @PostMapping("/{groupId}")
    public ResponseEntity<ShoppingListItemDto> addItem(
            @PathVariable UUID groupId,
            @RequestBody AddShoppingListItemRequest request) {
        User currentUser = authController.getLoggedInUser();
        return ResponseEntity.ok(shoppingListService.addItem(groupId, request, currentUser));
    }

    @PatchMapping("/item/{itemId}/toggle")
    public ResponseEntity<ShoppingListItemDto> toggleBought(@PathVariable UUID itemId) {
        User currentUser = authController.getLoggedInUser();
        return ResponseEntity.ok(shoppingListService.toggleBought(itemId, currentUser));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        User currentUser = authController.getLoggedInUser();
        shoppingListService.deleteItem(itemId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/clear")
    public ResponseEntity<Void> clearList(@PathVariable UUID groupId) {
        User currentUser = authController.getLoggedInUser();
        shoppingListService.clearList(groupId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/bought")
    public ResponseEntity<Void> removeBought(@PathVariable UUID groupId) {
        User currentUser = authController.getLoggedInUser();
        shoppingListService.deleteBoughtItems(groupId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ── Ingredient search (no group membership required) ──────────────────────

    @GetMapping("/ingredients/search")
    public ResponseEntity<List<ShoppingListItemDto>> searchIngredients(@RequestParam String q) {
        return ResponseEntity.ok(shoppingListService.searchIngredients(q));
    }
}
