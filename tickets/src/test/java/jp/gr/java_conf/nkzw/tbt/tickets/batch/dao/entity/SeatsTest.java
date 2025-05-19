package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity;

import org.junit.jupiter.api.Test;

public class SeatsTest {
    @Test
    void testClone() {
        Seats original = new Seats();
        original.setId(1);
        original.setRowNo(2);
        original.setSeatNo(3);
        original.setSeatZone(4);
        original.setAssignedApplicationId(5);

        Seats cloned = (Seats) original.clone();

        // Check if the cloned object has the same values as the original
        assert original.getId() == cloned.getId();
        assert original.getRowNo() == cloned.getRowNo();
        assert original.getSeatNo() == cloned.getSeatNo();
        assert original.getSeatZone() == cloned.getSeatZone();
        assert original.getAssignedApplicationId() == cloned.getAssignedApplicationId();

        // Check if the cloned object is a different instance
        assert original != cloned;

    }
}
