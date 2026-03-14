package Repository;

import Models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepo {
    private Map<Long, Order> orderDB = new HashMap<>();

    public Order save(Order order) {
        if(order.getOrderId() == null){
            Long id= (long) (orderDB.size() + 1);
            order.setOrderId(id);
            orderDB.put(id, order);
        }else {
            //update
            orderDB.put(order.getOrderId(), order);
        }
        return orderDB.get(order.getOrderId());
    }

    public Order findById(Long id) {
        return orderDB.get(id);
    }

    public List<Order> findByUserId(Long userId) {
        List<Order> orders = new ArrayList<>();
        for(Order order : orderDB.values()){
            if(order.getUserId().equals(userId)){
                orders.add(order);
            }
        }
        return orders;
    }
}
