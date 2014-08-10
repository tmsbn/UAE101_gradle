package com.hololibs.easyuae.models;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * DataModel for Groups
 */

@Table("groups")
public class Group extends Model {

    @Key
    @Column("group_id")
    public int groupId;

    @Column("active")
    public boolean active;

    @Column("group_name")
    public String groupName;

    @Column("marked")
    public boolean marked = false;
}
