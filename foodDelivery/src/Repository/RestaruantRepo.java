package Repository;

import Models.Restaruant;

import java.util.HashMap;
import java.util.Map;

public class RestaruantRepo {
    private Map<Long, Restaruant> restaurantDB = new HashMap<>();

    public Restaruant save(Restaruant restaurant) {
        if(restaurant.getRestaurantId() == null){
            Long id= (long) (restaurantDB.size() + 1);
            restaurant.setRestaurantId(id);
            restaurantDB.put(id, restaurant);
        }else {
            //update
            restaurantDB.put(restaurant.getRestaurantId(), restaurant);
        }
        return restaurantDB.get(restaurant.getRestaurantId());
    }

    //
    public Restaruant findByName(String name) {
        for(Restaruant restaurant : restaurantDB.values()){
            if(restaurant.getRestaurantName().equals(name)){
                return restaurant;
            }
        }
        return null;
    }
}
