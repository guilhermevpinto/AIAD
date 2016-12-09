package lighthinking.agent.learning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Chromossome {
	
	public String content;
	public Double value;
	
	public Chromossome(String content) {
		this(content, -1);
	}
	
	public Chromossome(String content, double value) {
		this.content = content;
		this.value = value;
	}
	
	public static Comparator<Chromossome> comparator = new Comparator<Chromossome>() {
        @Override
        public int compare(Chromossome c1, Chromossome c2) {
            return c2.value.compareTo(c1.value);
        }           
    };
    
    public Chromossome cleanCopy() {
    	return new Chromossome(content);
    }
    
    public static void printGeneration(ArrayList<Chromossome> pop) {
    	for(Chromossome c : pop) {
    		System.out.println(c.content + " " + c.value);
    	}
    }
}
