package com.blackmorse.hattrick.promotions.model;

import java.util.List;

public interface IPromotion<T> {
    List<T> getUpTeams();
    List<T> getDownTeams();
}
