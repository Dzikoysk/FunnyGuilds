package net.dzikoysk.funnyguilds.util.runnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

import net.dzikoysk.funnyguilds.FunnyGuilds;

import org.bukkit.Bukkit;

public class Ticking implements Runnable {

	private static DecimalFormat df = new DecimalFormat("#,###.##");
	private transient long lastPoll = System.nanoTime();
	private final LinkedList<Double> history = new LinkedList<>();
	private static String result = "20.0";
	
	public Ticking(){
		history.add(Double.valueOf(20.0D));
	}
	
	public void start(){
		Bukkit.getScheduler().runTaskTimer(FunnyGuilds.getInstance(), this, 1000L, 50L);
	}
	
	@Override
	public void run(){
		long startTime = System.nanoTime();
		long timeSpent = (startTime - this.lastPoll) / 1000L;
		if (timeSpent == 0L) timeSpent = 1L;
		if (history.size() > 10) history.remove();
		double tps = 50000000.0D / timeSpent;
		if (tps <= 21.0D) history.add(Double.valueOf(tps));
		this.lastPoll = startTime;
		double avg = 0.0D;
		for (Double f : history) if(f != null) avg += f.doubleValue();
		df.setRoundingMode(RoundingMode.HALF_UP);
	    result = df.format((avg / history.size()));
	}
	
	public static String getTPS() { 
		return result;
	}
}