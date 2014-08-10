package com.hololibs.easyuae;

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
    int groupId;

    @Column("active")
    boolean active;

    @Column("group_name")
    String groupName;

    @Column("marked")
    boolean marked=false;
}
