package com.hololibs.easyuae;

import se.emilsjolander.sprinkles.QueryResult;
import se.emilsjolander.sprinkles.annotations.Column;


public class GroupResult implements QueryResult {


    @Column("group_id")
    int groupId;

    @Column("group_name")
    String groupName;

    @Column("emirates")
    String emirates;

    @Column("marked")
    boolean marked;
}
