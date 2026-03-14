import Models.ItemsDto;
import Models.Order;
import Models.User;
import Repository.UserRepo;
import Service.AllService;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        AllService allService = new AllService();
        //Restaurant Registration
        String rest1=allService.restaurantRegistration("Pizza Hut", "GST12345", "pizzahut@gmail.com", "1234567890");
        System.out.println(rest1);
        //Add Items in Catalog
        allService.addItemsInCatalog("Pizza Hut", "Pepperoni Pizza", 12, 10);
        allService.addItemsInCatalog("Pizza Hut", "Veggie Pizza", 10.5, 15);
        List<String> a=allService.addItemsInCatalog("Pizza Hut", "Cheese Pizza", 19, 20);
        System.out.print("[ ");
        for(String s: a){
            System.out.print(s+" ");
        }
        System.out.print(" ]");
        System.out.println();

        // User Registration
        String user1=allService.userRegisteration("Banti Das", "banti.das@example.com", "8409157529");
        System.out.println(user1);

        //search item
        List<ItemsDto> list=allService.searchItem("Pizza Hut", "Pepperoni Pizza");
        System.out.print("[ ");
        for(ItemsDto i : list){
            System.out.print("Item Name: "+i.getItemName()+" Price: "+i.getPrice()+" Quantity: "+i.getQuantity());
        }
        System.out.print(" ]");
        System.out.println();

        User user=allService.getAllUsers().get(0);
        //place order
        String res=allService.placeOrder(user.getUserId(), "Pizza Hut", "Pepperoni Pizza", 2);
        System.out.println(res);

        List<Order> allOrders=allService.getOrders(user.getUserId());
        for (Order o : allOrders) {
            System.out.println("Order Id: "+o.getOrderId()+" User Id: "+o.getUserId()+" Restaurant Id: "+o.getRestaurantId()+" Item Name: "+o.getItemName()+" Quantity: "+o.getQuantity()+" Total Price: "+o.getPrice());
        }


    }
}