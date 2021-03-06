package Billing.Strategies;

import Model.Apartment.Apartment;
import Model.Apartment.Ratio;
import Model.CONSTS;
import Model.WaterModel.*;

import java.util.HashMap;

public class Strategy1 extends Strategy{

    public Strategy1(Apartment apartment, HashMap<String, Water> waterTypes) {
        super(apartment, waterTypes);
    }

    @Override
    public double calculateBill() {
        return Math.ceil(getFixBill() + getExtraBill());
    }
    private double getFixBill(){
        int waterPerPersonPerMonth = CONSTS.waterPerPerson * CONSTS.daysInMonth;
        int allocatedWater = this.apartment.getPeople() * waterPerPersonPerMonth;
        Ratio ratio = this.apartment.getRatio();
        int corporationWaterRatio = ratio.getCorporationWaterRatio();
        int borewellWaterRatio = ratio.getBorewellWaterRatio();
        double allocatedCorpWater= ((double)allocatedWater / ( corporationWaterRatio + borewellWaterRatio) ) * (double)corporationWaterRatio;
        double allocatedCorpWaterCost = allocatedCorpWater * ((FixedRateWater)this.waterTypes.get(FixRateWaterType.CorporationWater.getWaterType())).getRate();
        double allocatedBorewellWater= ((double)allocatedWater / ( corporationWaterRatio + borewellWaterRatio) ) * (double)borewellWaterRatio;
        double allocatedBorewellWaterCost = allocatedBorewellWater * ((FixedRateWater)this.waterTypes.get(FixRateWaterType.BorewellWater.getWaterType())).getRate();
        //System.out.println(allocatedCorpWaterCost +" "+ allocatedBorewellWaterCost);
        return allocatedCorpWaterCost + allocatedBorewellWaterCost;
    }
    private double getExtraBill(){
        int waterPerPersonPerMonth = CONSTS.waterPerPerson * CONSTS.daysInMonth;
        int extraWater = this.apartment.getGuests() * waterPerPersonPerMonth;
        double tankerWaterCost = 0;
        int extraRemainWater  = extraWater;
        SlabRateWater tankerWater = (SlabRateWater)this.waterTypes.get(SlabRateWaterTypes.TankerWater.getWaterType());
        //System.out.println(tankerWater.getSlabs());
        for(Slab slab : tankerWater.getSlabs()){
            if(extraWater < slab.getSlabStart() || extraWater < 0) break;
            int slabWater = slab.getSlabEnd() - slab.getSlabStart() + 1;
            double slabRate = slab.getSlabRate();
            if(extraRemainWater <= slabWater){
                tankerWaterCost += extraRemainWater * slabRate;
                extraWater = -1;
            }
            else{
                tankerWaterCost += slabWater * slabRate;
                extraRemainWater -= slabWater;
            }
        }
        //System.out.println(tankerWaterCost);
        return tankerWaterCost;
    }
}
