package com.applissima.fitconnectdemo;

/**
 * Created by ilkerkuscan on 16/05/17.
 */

public class EventWorkSummary {

    private boolean showWorkSummary;
    private SummaryWorkPerson summaryWorkPerson;

    public EventWorkSummary(SummaryWorkPerson summaryWorkPerson, boolean showWorkSummary){
        this.summaryWorkPerson = summaryWorkPerson;
        this.showWorkSummary = showWorkSummary;
    }

    public boolean isShowWorkSummary() {
        return showWorkSummary;
    }

    public SummaryWorkPerson getSummaryWorkPerson() {
        return summaryWorkPerson;
    }

}
