package com.hololibs.easyuae;

import se.emilsjolander.sprinkles.QueryResult;
import se.emilsjolander.sprinkles.annotations.Column;

/**
 * Created by tmsbn on 7/31/14.
 */
public class HotlineDetails implements QueryResult {


    @Column("name")
    String emirateName;

    @Column("hotline_id")
    int hotlineId;

    @Column("active")
    boolean active;

    @Column("emirate_id")
    String emirateId;

    @Column("group_id")
    String groupId;

    @Column("hotline_name")
    String hotlineName;

    @Column("hotline_number")
    String hotlineNumber;


}
