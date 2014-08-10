package com.hololibs.easyuae.models;

import se.emilsjolander.sprinkles.QueryResult;
import se.emilsjolander.sprinkles.annotations.Column;

/**
 * Created by tmsbn on 7/31/14.
 */
public class HotlineDetails implements QueryResult {


    @Column("name")
    public String emirateName;

    @Column("hotline_id")
    public int hotlineId;

    @Column("active")
    public boolean active;

    @Column("emirate_id")
    public int emirateId;

    @Column("group_id")
    public String groupId;

    @Column("hotline_name")
    public String hotlineName;

    @Column("hotline_number")
    public String hotlineNumber;


}
