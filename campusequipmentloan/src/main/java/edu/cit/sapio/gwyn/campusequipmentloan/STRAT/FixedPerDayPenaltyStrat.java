package edu.cit.sapio.gwyn.campusequipmentloan.STRAT;

import org.springframework.stereotype.Component;

@Component
public class FixedPerDayPenaltyStrat implements PenaltyStrat {

    private static final double PENALTY_PER_DAY = 50.0;

    @Override
    public double calculatePenalty(long daysLate) {
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * PENALTY_PER_DAY;
    }
}
