package com.hololibs.easyuae;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * DataModel for Emirates
 */

@Table("hotlines")
public class Hotline extends Model{

    @Key
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