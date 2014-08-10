package com.hololibs.easyuae.models;

import se.emilsjolander.sprinkles.QueryResult;
import se.emilsjolander.sprinkles.annotations.Column;


public class GroupResult implements QueryResult {


    @Column("group_id")
    public int groupId;

    @Column("group_name")
    public String groupName;

    @Column("emirates")
    public String emirates;

    @Column("marked")
    public boolean marked;
}
