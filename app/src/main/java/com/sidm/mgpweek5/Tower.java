package com.sidm.mgpweek5;

/**
 * Created by Foo on 6/12/2016.
 */

public class Tower extends Entity {

    // Constructor
    protected Tower(int hp) {
        super(hp);
        type = "tower";
        spriteArray = new Spriteanimation[1];
    }

    // Update
    @Override
    public void Update(float dt) {

    }

}
