/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.transactionbalancing.domain.solver;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.examples.transactionbalancing.domain.Computer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
/**
 * initiated and ready to use timeSlots is maintained in construction and reset
 */
public class ComputerPlan {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private Computer computer;
    private List<TimeSlot> timeSlots;
    private long busyTime;// save the total busy time for sake of access performance
    private long idleTime;

    public ComputerPlan(Computer computer) {
        this.computer = computer;
        timeSlots = new ArrayList<>();
        this.busyTime = 0;
        this.idleTime = 0;
    }

    /**
     * set the slot start time and add it to the slots list
     *
     * @param slotToAdd
     */
    public void addTimeSlot(TimeSlot slotToAdd) {
        long newSlotStart;
        if (timeSlots.size() == 0) {
            newSlotStart = 0;
        } else {
            TimeSlot lastSlot = timeSlots.get(timeSlots.size() - 1);
            newSlotStart = lastSlot.getSlotStart() + lastSlot.getBusyDuration();
        }
        slotToAdd.setSlotStart(newSlotStart);
        timeSlots.add(slotToAdd);
        this.busyTime += slotToAdd.getBusyDuration();
        this.idleTime += slotToAdd.getIdleDuration();
    }

    /**
     * remove the slot and update the start time of the slots after that one to be correct again
     *
     * @param slotToRemove
     */
    public void removeTimeSlot(TimeSlot slotToRemove) {
//        logger.trace("removeTimeSlot - slotToRemove: "+slotToRemove +", timeSlots: "+timeSlots);
        int index = timeSlots.indexOf(slotToRemove);
        if (index != -1 && index < timeSlots.size() - 1) {//if found an index and if it is not the last item of the list
            for (int i = index + 1; i < timeSlots.size(); i++) {
                /*
                shift the start time of the slots after removed slot, backward
                 */
                if(slotToRemove.getBusyDuration() != timeSlots.get(index).getBusyDuration()){
                    logger.error("Invalid status, the busy time is not synch. For event: "+slotToRemove.getBusyDuration() +
                            " and for plan:"+slotToRemove.getBusyDuration());
                }
                timeSlots.get(i).shiftSlotStart(-1 * slotToRemove.getBusyDuration());

                /*todo slotToRemove.getBusyDuration() is not working and the idle time is not synch, why?*/
//                timeSlots.get(i).shiftSlotStart(-1 * timeSlots.get(index).getBusyDuration());//temp

            }
        }
        if (index != -1) {
            Boolean result = timeSlots.remove(slotToRemove);
            if (!result) {
                throw new RuntimeException("Unexpectedly can't remove slot:" + slotToRemove + ".");
            }
            this.busyTime -= slotToRemove.getBusyDuration();
            this.idleTime -= slotToRemove.getIdleDuration();
        } else {
            logger.error("can not find the removal requested slot as: " + slotToRemove);
        }
//        logger.trace("removeTimeSlot - done");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComputerPlan-").append(computer.getId()).append(":{busy=").append(busyTime).
                append(", slots:").append(timeSlots.toString()).append("}");
        return sb.toString();
    }

    public void reset() {
        timeSlots.clear();
        busyTime = 0;
        idleTime = 0;
    }

    /**
     * return the planned process at the given time and in absence of any process return -1
     * Note: [start time, end time) starting included and ending not included
     * Note: this search algorithm is basic and can be improved by binary in case of need for better performance
     *
     * @param askedTime
     * @return
     */
    public Long getProcessAt(Long askedTime) {
        if (timeSlots == null || timeSlots.size() == 0 || askedTime < timeSlots.get(0).getSlotStart() || busyTime < askedTime) {
            return -1l;
        } else {
            for (TimeSlot slot : timeSlots) {
                if (slot.isActiveAt(askedTime)) {
                    return slot.getProcess().getId();
                } else {
                    if (slot.getSlotStart() + slot.getBusyDuration() < askedTime) {
                        continue;
                    } else {
                        return -1l;
                    }
                }
            }
        }
//        throw new RuntimeException("getProcessAt time = " + askedTime + " for timeSlots = " + timeSlots + " did not " +
//                "return value. ");
        return -1l;//todo the exception is thrown at times that the first if is true, check it by breakpoint here
    }


}
