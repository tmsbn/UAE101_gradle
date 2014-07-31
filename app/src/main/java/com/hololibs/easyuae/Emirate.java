package com.hololibs.easyuae;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

/**
 * DataModel for Emirates
 */

@Table("emirates")
public class Emirate extends Model{

    @Key
    @Column("emirate_id")
    int emirateId;

    @Column("name")
    String name;

    @Column("shortform")
    String shortform;
}