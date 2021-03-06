package agh.ics.oop;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.out;

public class Animal {


    private int energy;
    private Vector2d position;
    private int[] genotype;
    private int direction;
    private AbstractMap map;
    private int energyLoss;
    private int lifeLength;
    private int childrenNumber;
    private Animal trackable_ancestor;
    private int descendants_number = 0;
    private boolean is_tracked = false;
    private ArrayList<IPositionChangeObserver> maplist;
    public Animal(AbstractMap map, Vector2d position, int[] genotype, int energy, int direction)
    {
        this.map = map;
        this.trackable_ancestor = null;
        this.energyLoss = this.map.energyLoss;
        this.energy = energy;
        this.genotype = genotype;
        this.position = position;
        this.direction = direction;
        this.maplist = new ArrayList<>();
        this.lifeLength = 0;
        this.childrenNumber = 0;
    }

    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Animal))
            return false;
        Animal that = (Animal) other;
        return that.genotype == this.genotype && that.position == this.position && that.direction == this.direction && that.energy == this.energy;
    }

    public int getEnergy()
    {
        return this.energy;
    }

    public int[] getGenotype()
    {
        return this.genotype;
    }

    public Vector2d getPosition()
    {
        return this.position;
    }

    private int getDirection()
    {
        return this.direction;
    }

    public int spin()
    {
        return genotype[(new Random()).nextInt(genotype.length)];
    }

    public void subtractEnergy(int energyloss)
    {
        this.energy = this.energy - energyloss;
    }

    public void addEnergy(int energygain)
    {
        this.energy = this.energy + energygain;
    }

    public boolean outOfBounds(Vector2d position)
    {
        return !(this.map.lower_left.precedes(position) && this.map.upper_right.follows(position));
    }

    public Vector2d modifyOutOfBoundsPosition(Vector2d position)
    {
        if (position.x > this.map.upper_right.x && position.y > this.map.upper_right.y)
        {
            position = new Vector2d(0, 0);
        }
        else if(position.x > this.map.upper_right.x && position.y < this.map.lower_left.y)
        {
            position = new Vector2d(0, this.map.upper_right.y);
        }
        else if(position.x < this.map.lower_left.x && position.y> this.map.upper_right.y)
        {
            position = new Vector2d(this.map.upper_right.x, 0);
        }
        else if (position.x > this.map.upper_right.x)
        {
            position = new Vector2d(0, position.y);
        }
        else if (position.y > this.map.upper_right.y)
        {
            position = new Vector2d(position.x, 0);
        }
        else if (position.x < this.map.lower_left.x && position.y < this.map.lower_left.y)
        {
            position = this.map.upper_right;
        }
        else if (position.x < this.map.lower_left.x)
        {
            position = new Vector2d(this.map.upper_right.x, position.y);
        }
        else if (position.y < this.map.lower_left.y)
        {
            position = new Vector2d(position.x, this.map.upper_right.y);
        }
        return position;
    }

    public int getChildrenNumber()
    {
        return this.childrenNumber;
    }

    public String toString()
    {
        return(String.valueOf(this.direction));
    }

    public void move_on_the_map(int orientation)
    {
        Vector2d new_position = new Vector2d(0, 0);
        switch(orientation)
        {
            case 0:
            {
                switch(this.direction)
                {
                    case 0 -> new_position = this.position.add(new Vector2d (0,1));
                    case 1 -> new_position = this.position.add(new Vector2d (1,1));
                    case 2 -> new_position = this.position.add(new Vector2d (1,0));
                    case 3 -> new_position = this.position.add(new Vector2d (1,-1));
                    case 4 -> new_position = this.position.add(new Vector2d (0,-1));
                    case 5 -> new_position = this.position.add(new Vector2d (-1,-1));
                    case 6 -> new_position = this.position.add(new Vector2d (-1,0));
                    case 7 -> new_position = this.position.add(new Vector2d (-1,1));
                }

            }
            case 4:
            {
                switch(this.direction)
                {
                    case 0 -> new_position = this.position.subtract(new Vector2d (0,1));
                    case 1 -> new_position = this.position.subtract(new Vector2d (1,1));
                    case 2 -> new_position = this.position.subtract(new Vector2d (1,0));
                    case 3 -> new_position = this.position.subtract(new Vector2d (1,-1));
                    case 4 -> new_position = this.position.subtract(new Vector2d (0,-1));
                    case 5 -> new_position = this.position.subtract(new Vector2d (-1,-1));
                    case 6 -> new_position = this.position.subtract(new Vector2d (-1,0));
                    case 7 -> new_position = this.position.subtract(new Vector2d (-1,1));
                }
            }
            if (this.map instanceof FoldableMap && outOfBounds(new_position))
            {
                new_position = modifyOutOfBoundsPosition(new_position);
            }
            if (this.map.canMoveTo(new_position))
            {
                positionChanged(this.position, new_position);
                this.position = new_position;
            }

        }
    }

    public void move()
    {
        int spin = this.spin();
        int new_direction = (this.getDirection()+spin)%8;
        switch(spin)
        {
            case 0:
            {
                this.move_on_the_map(0);
                break;
            }
            case 4:
            {
                this.move_on_the_map(4);
                break;
            }
            default:
            {
                this.direction = new_direction;
            }
        }
    }

    public void addObserver(IPositionChangeObserver observer)
    {
        this.maplist.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer)
    {
        this.maplist.remove(observer);
    }

    private void positionChanged(Vector2d old_position, Vector2d new_position)
    {
        if (this.maplist.size() > 0)
            for (IPositionChangeObserver observers : this.maplist)
            {
                observers.positionChanged(this, old_position, new_position);
            }

    }

    public void setAncestor(Animal ancestor)
    {
        this.trackable_ancestor = ancestor;
    }

    public int getDescendants_number()
    {
        return this.descendants_number;
    }

    public Animal getTrackable_ancestor()
    {
        return this.trackable_ancestor;
    }

    public boolean get_if_tracked()
    {
        return this.is_tracked;
    }

    public void get_to_track(Animal tracked)
    {
        tracked.is_tracked = true;
    }

    public void addDescendant()
    {
        this.descendants_number += 1;
    }



    public void addAChild()
    {
        this.childrenNumber += 1;
    }

    public void anotherDaySurvived()
    {
        this.lifeLength += 1;
    }

    public int getLifeLength()
    {
        return this.lifeLength;
    }

    public int getMapStartEnergy()
    {
        return this.map.startEnergy;
    }


}
