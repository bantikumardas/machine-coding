package Service;

import Models.*;
import Repository.ItemCatalogRepo;
import Repository.OrderRepo;
import Repository.RestaruantRepo;
import Repository.UserRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllService {
    private UserRepo userRepo;
    private OrderRepo orderRepo;
    private RestaruantRepo restaruantRepo;
    private ItemCatalogRepo itemCatalogRepo;

    public AllService() {
        this.userRepo = new UserRepo();
        this.orderRepo = new OrderRepo();
        this.restaruantRepo = new RestaruantRepo();
        this.itemCatalogRepo =new ItemCatalogRepo();
    }


    //User Registration
    public String userRegisteration(String userName, String email, String phoneNumber) {
        if(phoneNumber.length() != 10)
            throw new RuntimeException("Invalid phone number");
        if(isEmailExist(email))
            throw new RuntimeException("Email already exist");
        if(userName == null || userName.isEmpty())
            throw new RuntimeException("User name cannot be empty");
        User user = new User(userName, email, phoneNumber);
        User saveUser=userRepo.save(user);
        return "User Registered!!!  UserId: "+saveUser.getUserId();
    }

    //Restaurant Registration
    public String restaurantRegistration(String restaurantName, String GSTNumber, String emailId, String phoneNumber) {
        if(phoneNumber.length() != 10){
            throw new RuntimeException("Invalid phone number");
        }
        Restaruant restaurant = new Restaruant(restaurantName, GSTNumber, emailId, phoneNumber);
        Restaruant savedRest=restaruantRepo.save(restaurant);
        return "Restaurant Registered!!!  RestaurantId: "+savedRest.getRestaurantId();
    }

    //addItemsInCatalog
    public List<String> addItemsInCatalog(String restaurantName, String itemName, double price, int quantity) {
        Restaruant restaurant = restaruantRepo.findByName(restaurantName);
        if(restaurant == null){
            return new ArrayList<>(Collections.singleton("Restaurant not found"));
        }
        Long restaurantId=restaurant.getRestaurantId();
        CatalogItems item=new CatalogItems();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setRestaurantId(restaurantId);
        CatalogItems savedItem=itemCatalogRepo.save(item);
        List<CatalogItems> items=itemCatalogRepo.findByRestaurantId(restaurantId);
        List<String> itemNames=new ArrayList<>();
        for(CatalogItems i : items){
            itemNames.add(i.getItemName());
        }
        return itemNames;
    }

    //search Items
    public List<ItemsDto> searchItem(String restaurantName, String itemName) {

        Restaruant restaurant = restaruantRepo.findByName(restaurantName);
        if(restaurant == null){
            throw new RuntimeException("Restaurant not found");
        }

        List<CatalogItems> result =itemCatalogRepo.findByRestaurantId(restaurant.getRestaurantId());
        //sort item by price on ascending order
        result.sort((i1, i2) -> Double.compare(i1.getPrice(), i2.getPrice()));
        List<ItemsDto> itemsDtoList=new ArrayList<>();
        for(CatalogItems item : result){
            if(item.getItemName().equalsIgnoreCase(itemName)){
                ItemsDto itemsDto=new ItemsDto(item.getItemName(), item.getPrice(), item.getQuantity());
                itemsDtoList.add(itemsDto);
            }
        }
        if(itemsDtoList.isEmpty()){
            throw new RuntimeException("Item not found in restaurant menu");
        }
        return itemsDtoList;
    }

    // Place Order
    public String placeOrder(Long userId, String restaurantName, String itemName, int quantity) {
        Restaruant restaurant = restaruantRepo.findByName(restaurantName);
        if(restaurant == null){
            throw  new RuntimeException("Restaurant not found");
        }
        if(quantity <= 0){
            throw new RuntimeException("Quantity must be greater than zero");
        }
        if(userRepo.findById(userId) == null){
            throw new RuntimeException("User not found");
        }

        //menu of restaurant
        List<CatalogItems> item = itemCatalogRepo.findByRestaurantId(restaurant.getRestaurantId());
        //check item is available in menu or not
        CatalogItems orderedItem = null;
        for(CatalogItems i : item){
            if(i.getItemName().equalsIgnoreCase(itemName)){
                orderedItem = i;
                break;
            }
        }
        if(orderedItem == null){
            throw  new RuntimeException("Item not found in restaurant menu");
        }
        //check quantity
        if(orderedItem.getQuantity() < quantity){
            throw new RuntimeException("Required quantity not available");
        }
        //if item is not sufficient in quantity then throw exception
        if(orderedItem.getQuantity() < quantity){
            throw new RuntimeException("Required quantity not available");
        }

        //update quantity in menu
        orderedItem.setQuantity(orderedItem.getQuantity() - quantity);
        itemCatalogRepo.save(orderedItem);
        //place order
        Order order=new Order();
        order.setUserId((long) userId);
        order.setRestaurantId(restaurant.getRestaurantId());
        order.setRestaurantName(restaurant.getRestaurantName());
        order.setItemName(orderedItem.getItemName());
        order.setQuantity(quantity);
        order.setPrice(orderedItem.getPrice() * quantity);
        order.setOrderStatus(OrderStatus.PLACED);
        Order placedOrder=orderRepo.save(order);

        return "Order placed successfully orderId: "+placedOrder.getOrderId();
    }

    // Get Orders
    public List<Order> getOrders(Long userId) {
        User user = userRepo.findById((long) userId);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        //find all order by userId
        List<Order> orders=orderRepo.findByUserId((long) userId);
        return orders;
    }

    // Cancel Order
    public String cancelOrder(Long userId, Long orderId) {
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Order order = orderRepo.findById( orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);
        //update quantity in menu
        List<CatalogItems> items = itemCatalogRepo.findByRestaurantId(order.getRestaurantId());
        for (CatalogItems item : items) {
            if (item.getItemName().equalsIgnoreCase(order.getItemName())) {
                item.setQuantity(item.getQuantity() + order.getQuantity());
                itemCatalogRepo.save(item);
                break;
            }
        }
        return "Order " + orderId + " canceled successfully.";
    }

    //get all user
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public boolean isEmailExist(String email) {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

}
