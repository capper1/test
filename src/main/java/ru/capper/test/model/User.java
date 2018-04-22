package ru.capper.test.model;

import ru.capper.test.dao.Identified;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Объектное представление сущности Судент.
 */
public class User implements Identified<Integer> {

    private Integer id = null;
    private BigDecimal countPong = BigDecimal.ZERO;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getCountPong() {
        return countPong;
    }

    public void setCountPong(BigDecimal countPong) {
        this.countPong = countPong;
    }
}
