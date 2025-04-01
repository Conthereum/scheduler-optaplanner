package org.optaplanner.examples.transactionbalancing.domain.solver;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.examples.transactionbalancing.domain.EmvProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Getter
@Setter
//@EqualsAndHashCode//because of changing start time in process, this is not functional
/**
 * We make the timeslots unique just based on the process id, so assuming that processes are unique, then the slots
 * are making unique based on their processes id and in another word we do not have two timeslot with the same
 * process id in one solution.
 *
 * each slot includes one optional idle time and then compulsory execution time.
 */
public class TimeSlot {
    private long slotStart;
    private long idleDuration; //equal or greater than zero, default value as zero
    private long exeDuration;
    //    private TimeSlotType slotType;
    private EmvProcess process;

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Shift forward or backward the start time, this method just add the positive/negative input with the current
     * start time
     *
     * @param timeToShift
     */
    public void shiftSlotStart(long timeToShift) {
        setSlotStart(slotStart + timeToShift);
    }

    public void setSlotStart(long slotStart) {
        if(slotStart<0){
            logger.error("negative start time happened");
        }
        this.slotStart = slotStart;
    }

    //
//    enum TimeSlotType {
//        IDLE, TXN
//    }

    public TimeSlot(EmvProcess process) {
        this.process = process;
//        this.slotType = TimeSlotType.TXN;
        this.exeDuration = process.getEstimatedExecutionTime();
        this.idleDuration = process.getIdleTimeBeforeProcess()==null ? 0: process.getIdleTimeBeforeProcess();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{TXN-" + process.getId())
                .append(", start(").append(slotStart).append("):")
                .append(" IDLE(" + idleDuration + ")-")
                .append("EXE(" + exeDuration + ")").append(
                        "} ");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        Boolean result = process.getId() == timeSlot.process.getId() && process.getEstimatedExecutionTime() == timeSlot.process.getEstimatedExecutionTime();
        return result;
    }

    @Override
    public int hashCode() {
        //todo why by using id it is buggy?
        return Objects.hash(process.getId(), process.getEstimatedExecutionTime());
    }

    /**
     * Note: [start time, end time) starting included and ending not included
     * @param askedTime
     * @return
     */
    public Boolean isActiveAt(Long askedTime) {
        if (getExeStart() <= askedTime && //exeStart<=askedTime
                askedTime < getEnd()) {//askedTime<exeEnd
            return true;
        } else {
            return false;
        }
    }

    public Long getBusyDuration() {
        return this.idleDuration + this.exeDuration;
    }

    public long getExeStart(){
        return this.slotStart + this.idleDuration;
    }

    public long getEnd(){
        return this.slotStart + this.idleDuration + this.exeDuration;
    }

}