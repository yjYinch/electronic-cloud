package com.sedwt.workflow.mapper;

import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.Meeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingMapper {
    List<Meeting> getFutureMeetings(Appraisal appraisal);

    Integer deleteAppraisalMeetings(Long id);

    Integer createMeeting(Meeting meeting);

    Integer updateMeeting(Meeting meeting);

    Meeting selectMeetingByAppraisalId(Long appraisalId);
}
