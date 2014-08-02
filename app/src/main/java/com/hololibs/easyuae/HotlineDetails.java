package com.hololibs.easyuae;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.QueryResult;
import se.emilsjolander.sprinkles.annotations.Column;

/**
 * Created by tmsbn on 7/31/14.
 */
public class HotlineDetails implements QueryResult {


    @Column("name")
    String emirateName;

    @Column("hotline_name")
    String hotlineName;

    @Column("hotline_number")
    String hotlineNumber;


}
