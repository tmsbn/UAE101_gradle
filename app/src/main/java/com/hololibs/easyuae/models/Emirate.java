package com.hololibs.easyuae.models;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * DataModel for Emirates
 */

@Table("emirates")
public class Emirate extends Model {

    @Key
    @Column("emirate_id")
    public int emirateId;

    @Column("name")
    public String name;

    @Column("shortform")
    public String shortform;
}