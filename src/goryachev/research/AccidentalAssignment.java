package goryachev.research;

public class AccidentalAssignment {
//    public int accidental(int x)
//    {
//        boolean rv;
//        if(rv = (x == 0)) {
//            return 1;
//        }
//        return 2;
//    }
    
    public int explicit(int x)
    {
        boolean rv;
        if((rv = (x == 0)) == true) {
            return 1;
        }
        return 2;
    }
}
