package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.AddShoppingListItemRequest;
import dk.dtu._62595.demo.dto.ShoppingListItemDto;
import dk.dtu._62595.demo.services.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping("/{groupId}")
    public List<ShoppingListItemDto> getItems(@PathVariable UUID groupId) {
        return shoppingListService.getItemsForGroup(groupId);
    }

    @PostMapping("/{groupId}")
    public ShoppingListItemDto addItem(@PathVariable UUID groupId,
                                       @RequestBody AddShoppingListItemRequest request) {
        return shoppingListService.addItem(groupId, request);
    }

    @PatchMapping("/item/{itemId}/toggle")
    public ShoppingListItemDto toggleBought(@PathVariable UUID itemId) {
        return shoppingListService.toggleBought(itemId);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        shoppingListService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/clear")
    public ResponseEntity<Void> clearList(@PathVariable UUID groupId) {
        shoppingListService.clearList(groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/bought")
    public ResponseEntity<Void> deleteBought(@PathVariable UUID groupId) {
        shoppingListService.deleteBoughtItems(groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ingredients/search")
    public List<ShoppingListItemDto> searchIngredients(@RequestParam String q) {
        return shoppingListService.searchIngredients(q);
    }
}
