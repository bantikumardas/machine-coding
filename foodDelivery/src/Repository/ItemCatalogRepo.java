package Repository;

import Models.CatalogItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemCatalogRepo {
    private Map<Long, CatalogItems> itemDb = new HashMap<>();

    public CatalogItems save(CatalogItems items) {
        if(items.getId() == null){
            Long id= (long) (itemDb.size() + 1);
            items.setId(id);
            itemDb.put(id, items);
        }else {
            //update
            itemDb.put(items.getId(), items);
        }
        return itemDb.get(items.getId());
    }

    public CatalogItems findById(Long id) {
        return itemDb.get(id);
    }
    public List<CatalogItems> findAll() {
        return new ArrayList<>(itemDb.values());
    }

    public List<CatalogItems> findByRestaurantId(Long restaurantId) {
        List<CatalogItems> itemsList = new ArrayList<>();
        for(CatalogItems item : itemDb.values()){
            if(item.getRestaurantId().equals(restaurantId)){
                itemsList.add(item);
            }
        }
        return itemsList;
    }
}
