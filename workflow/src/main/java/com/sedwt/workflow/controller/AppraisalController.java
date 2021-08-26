package com.sedwt.workflow.controller;

import com.sedwt.common.entity.R;
import com.sedwt.common.utils.ServletUtils;
import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.service.AppraisalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("appraisal")
@Slf4j
public class AppraisalController {

    @Autowired
    private AppraisalService appraisalService;

    /**
     * 获取评审列表
     *
     * @param appraisal 评审信息
     * @return 评审列表结果
     */
    @GetMapping("/fetch")
    public R fetch(Appraisal appraisal) {
        // TODO
        Long currentUserId = ServletUtils.getCurrentUserId();
        //startPage();
        //return result(appraisalService.selectAppraisalList(appraisal, currentUserId));
        return null;
    }

    /**
     * 获取单个评审详情
     *
     * @param id 评审id
     * @return 评审详情结果
     */
    @GetMapping("/queryById")
    public R queryById(@RequestParam(name = "id") Long id) {
        Long currentUserId = ServletUtils.getCurrentUserId();
        return appraisalService.selectAppraisalById(id, currentUserId);
    }

    /**
     * 根据id撤销评审
     *
     * @param appraisal 评审信息
     * @return 撤销结果
     */
    @GetMapping("/cancel")
    public R cancel(Appraisal appraisal) {
        return appraisalService.cancelAppraisalById(appraisal);
    }

    /**
     * 新增评审
     *
     * @return 新增结果
     */
    @GetMapping("/create")
    public R create() {
        Long currentUserId = ServletUtils.getCurrentUserId();
        return appraisalService.addAppraisal(currentUserId);
    }

    /**
     * 提交评审
     *
     * @param appraisal 评审信息
     * @return 创建结果
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Appraisal appraisal) {
        //获取当前登录用户id
        Long currentUserId = ServletUtils.getCurrentUserId();
        return appraisalService.editAppraisal(appraisal, currentUserId);
    }

    /**
     * 开始评审
     *
     * @param appraisal 评审信息
     * @return 更新结果
     */
    @PostMapping("/start")
    public R start(@RequestBody Appraisal appraisal) {
        // 设置评审状态为开始
        appraisal.setStatus(2);
        //获取当前登录用户id
        Long currentUserId = ServletUtils.getCurrentUserId();
        return appraisalService.processAppraisal(appraisal, currentUserId);
    }

    /**
     * 完成评审
     *
     * @param appraisal 评审信息
     * @return 更新结果
     */
    @PostMapping("/complete")
    public R complete(@RequestBody Appraisal appraisal) {
        // 设置评审状态为完成
        appraisal.setStatus(3);
        //获取当前登录用户id
        Long currentUserId = ServletUtils.getCurrentUserId();
        return appraisalService.processAppraisal(appraisal, currentUserId);
    }

    /**
     * 删除评审
     *
     * @param id 评审id
     * @return Result
     */
    @DeleteMapping("/delete")
    public R delete(@RequestParam(name = "id") Long id) {
        return appraisalService.deleteById(id);
    }
}
