package jp.gr.java_conf.nkzw.tbt.tickets.batch.dao.entity;

import org.junit.jupiter.api.Test;

public class ApplicationsTest {
    @Test
    void testClone() {
        Applications original = new Applications();
        original.setId(1);
        original.setSeatZone(2);
        original.setApplyNum(3);
        original.setAssignedFlag(4);

        Applications cloned = (Applications) original.clone();

        // Check if the cloned object has the same values as the original
        assert original.getId() == cloned.getId();
        assert original.getSeatZone() == cloned.getSeatZone();
        assert original.getApplyNum() == cloned.getApplyNum();
        assert original.getAssignedFlag() == cloned.getAssignedFlag();

        // Check if the cloned object is a different instance
        assert original != cloned;

    }
}
