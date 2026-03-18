package dk.dtu._62595.demo.services;

import dk.dtu._62595.demo.dto.AddShoppingListItemRequest;
import dk.dtu._62595.demo.dto.ShoppingListItemDto;
import dk.dtu._62595.demo.model.Group;
import dk.dtu._62595.demo.model.Ingredient;
import dk.dtu._62595.demo.model.ShoppingList;
import dk.dtu._62595.demo.model.User;
import dk.dtu._62595.demo.repositories.GroupMemberRepository;
import dk.dtu._62595.demo.repositories.GroupRepository;
import dk.dtu._62595.demo.repositories.IngredientRepository;
import dk.dtu._62595.demo.repositories.ShoppingListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final IngredientRepository ingredientRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public ShoppingListService(ShoppingListRepository shoppingListRepository,
                               IngredientRepository ingredientRepository,
                               GroupRepository groupRepository,
                               GroupMemberRepository groupMemberRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.ingredientRepository = ingredientRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    // Helper

    private Group requireGroupMember(UUID groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
        boolean isMember = groupMemberRepository
                .findByGroupAndUser(group, currentUser)
                .isPresent();
        if (!isMember) {
            throw new SecurityException("You are not a member of this group");
        }
        return group;
    }

    // Read

    public List<ShoppingListItemDto> getItemsForGroup(UUID groupId, User currentUser) {
        Group group = requireGroupMember(groupId, currentUser);
        return shoppingListRepository.findByGroup(group)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Write

    public ShoppingListItemDto addItem(UUID groupId, AddShoppingListItemRequest request, User currentUser) {
        Group group = requireGroupMember(groupId, currentUser);

        Ingredient ingredient = ingredientRepository
                .findByName(request.ingredientName().trim())
                .orElseGet(() -> ingredientRepository.save(
                        new Ingredient(request.ingredientName().trim(), null, null, null, null, null, null, null, null)
                ));

        // If item already exists for this group, merge the amount
        return shoppingListRepository
                .findByGroupAndIngredient(group, ingredient)
                .stream()
                .findFirst()
                .map(existing -> {
                    shoppingListRepository.deleteById(existing.getId());
                    return toDto(shoppingListRepository.save(new ShoppingList(
                            group, ingredient,
                            existing.getAmount() + request.amount(),
                            request.unit(),
                            existing.isBought()
                    )));
                })
                .orElseGet(() -> toDto(shoppingListRepository.save(
                        new ShoppingList(group, ingredient, request.amount(), request.unit(), false)
                )));
    }

    public ShoppingListItemDto toggleBought(UUID itemId, User currentUser) {
        ShoppingList item = shoppingListRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        requireGroupMember(item.getGroup().getId(), currentUser);
        shoppingListRepository.deleteById(itemId);
        return toDto(shoppingListRepository.save(
                new ShoppingList(item.getGroup(), item.getIngredient(), item.getAmount(), item.getUnit(), !item.isBought())
        ));
    }

    public void deleteItem(UUID itemId, User currentUser) {
        ShoppingList item = shoppingListRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        requireGroupMember(item.getGroup().getId(), currentUser);
        shoppingListRepository.deleteById(itemId);
    }

    public void clearList(UUID groupId, User currentUser) {
        Group group = requireGroupMember(groupId, currentUser);
        shoppingListRepository.findByGroup(group)
                .forEach(item -> shoppingListRepository.deleteById(item.getId()));
    }

    public void deleteBoughtItems(UUID groupId, User currentUser) {
        Group group = requireGroupMember(groupId, currentUser);
        shoppingListRepository.findByGroupAndIsBought(group, true)
                .forEach(item -> shoppingListRepository.deleteById(item.getId()));
    }

    public List<ShoppingListItemDto> searchIngredients(String query) {
        return ingredientRepository
                .searchByRelevance(query)
                .stream()
                .map(i -> new ShoppingListItemDto(null, i.getId(), i.getName(), 0, null, false))
                .toList();
    }

    // Mapping

    private ShoppingListItemDto toDto(ShoppingList item) {
        return new ShoppingListItemDto(
                item.getId(),
                item.getIngredient().getId(),
                item.getIngredient().getName(),
                item.getAmount(),
                item.getUnit(),
                item.isBought()
        );
    }
}
