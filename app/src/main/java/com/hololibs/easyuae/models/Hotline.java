package com.hololibs.easyuae.models;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * DataModel for Hotline
 */

@Table("hotlines")
public class Hotline extends Model {

    @Key
    @Column("hotline_id")
    public int hotlineId;

    @Column("active")
    public boolean active;

    @Column("emirate_id")
    public String emirateId;

    @Column("group_id")
    public String groupId;

    @Column("hotline_name")
    public String hotlineName;

    @Column("hotline_number")
    public String hotlineNumber;

}