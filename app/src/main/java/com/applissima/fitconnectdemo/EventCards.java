package com.applissima.fitconnectdemo;

import java.util.List;

/**
 * Created by ilkerkuscan on 16/05/17.
 */

public class EventCards {

    private List<FitWorkPersonCard> cards;

    public EventCards(List<FitWorkPersonCard> dataset) {
        this.cards = dataset;
    }

    public List<FitWorkPersonCard> getCards(){
        return cards;
    }
}