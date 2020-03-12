package io.metersphere.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.base.domain.User;
import io.metersphere.commons.constants.RoleConstants;
import io.metersphere.commons.utils.PageUtils;
import io.metersphere.commons.utils.Pager;
import io.metersphere.controller.request.UserRequest;
import io.metersphere.controller.request.member.AddMemberRequest;
import io.metersphere.controller.request.member.QueryMemberRequest;
import io.metersphere.controller.request.organization.AddOrgMemberRequest;
import io.metersphere.controller.request.organization.QueryOrgMemberRequest;
import io.metersphere.dto.UserDTO;
import io.metersphere.service.OrganizationService;
import io.metersphere.service.UserService;
import io.metersphere.service.WorkspaceService;
import io.metersphere.user.SessionUser;
import io.metersphere.user.SessionUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RequestMapping("user")
@RestController
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private WorkspaceService workspaceService;

    // admin api
    @PostMapping("/special/add")
    @RequiresRoles(RoleConstants.ADMIN)
    public UserDTO insertUser(@RequestBody User user) {
        return userService.insert(user);
    }

    @PostMapping("/special/list/{goPage}/{pageSize}")
    @RequiresRoles(RoleConstants.ADMIN)
    public Pager<List<User>> getUserList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody UserRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getUserListWithRequest(request));
    }

    @GetMapping("/special/delete/{userId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public void deleteUser(@PathVariable(value = "userId") String userId) {
        userService.deleteUser(userId);
    }

    @PostMapping("/special/update")
    @RequiresRoles(RoleConstants.ADMIN)
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PostMapping("/special/ws/member/list/{goPage}/{pageSize}")
    @RequiresRoles(RoleConstants.ADMIN)
    public Pager<List<User>> getMemberListByAdmin(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getMemberList(request));
    }

    @PostMapping("/special/ws/member/list/all")
    @RequiresRoles(RoleConstants.ADMIN)
    public List<User> getMemberListByAdmin(@RequestBody QueryMemberRequest request) {
        return userService.getMemberList(request);
    }

    @PostMapping("/special/ws/member/add")
    @RequiresRoles(RoleConstants.ADMIN)
    public void addMemberByAdmin(@RequestBody AddMemberRequest request) {
        userService.addMember(request);
    }

    @GetMapping("/special/ws/member/delete/{workspaceId}/{userId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public void deleteMemberByAdmin(@PathVariable String workspaceId, @PathVariable String userId) {
        userService.deleteMember(workspaceId, userId);
    }

    @PostMapping("/special/org/member/add")
    @RequiresRoles(RoleConstants.ADMIN)
    public void addOrganizationMemberByAdmin(@RequestBody AddOrgMemberRequest request) {
        userService.addOrganizationMember(request);
    }

    @GetMapping("/special/org/member/delete/{organizationId}/{userId}")
    @RequiresRoles(RoleConstants.ADMIN)
    public void delOrganizationMemberByAdmin(@PathVariable String organizationId, @PathVariable String userId) {
        userService.delOrganizationMember(organizationId, userId);
    }

    @PostMapping("/special/org/member/list/{goPage}/{pageSize}")
    @RequiresRoles(RoleConstants.ADMIN)
    public Pager<List<User>> getOrgMemberListByAdmin(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryOrgMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getOrgMemberList(request));
    }

    @PostMapping("/special/org/member/list/all")
    @RequiresRoles(RoleConstants.ADMIN)
    public List<User> getOrgMemberListByAdmin(@RequestBody QueryOrgMemberRequest request) {
        return userService.getOrgMemberList(request);
    }
    // admin api

    @GetMapping("/list")
    @RequiresRoles(value = {RoleConstants.ADMIN,RoleConstants.ORG_ADMIN}, logical = Logical.OR)
    public List<User> getUserList() {
        return userService.getUserList();
    }

    @PostMapping("/update/currentuser")
    public UserDTO updateCurrentUser(@RequestBody User user) {
        SessionUser sessionUser = SessionUtils.getUser();
        BeanUtils.copyProperties(user, sessionUser);
        userService.updateUser(user);
        return SessionUtils.getUser();
    }

    @PostMapping("/switch/source/org/{sourceId}")
    @RequiresRoles(RoleConstants.ORG_ADMIN)
    public UserDTO switchOrganization(@PathVariable(value = "sourceId") String sourceId) {
        UserDTO user = SessionUtils.getUser();
        userService.switchUserRole(user,"organization",sourceId);
        return SessionUtils.getUser();
    }

    @PostMapping("/switch/source/ws/{sourceId}")
    @RequiresRoles(value = {RoleConstants.TEST_MANAGER,RoleConstants.TEST_VIEWER,RoleConstants.TEST_USER}, logical = Logical.OR)
    public UserDTO switchWorkspace(@PathVariable(value = "sourceId") String sourceId) {
        UserDTO user = SessionUtils.getUser();
        userService.switchUserRole(user, "workspace", sourceId);
        return SessionUtils.getUser();
    }

    @GetMapping("/info/{userId}")
    public User getUserInfo(@PathVariable(value = "userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * 获取工作空间成员用户
     */
    @PostMapping("/ws/member/list/{goPage}/{pageSize}")
    @RequiresRoles(value = {RoleConstants.ORG_ADMIN,RoleConstants.TEST_MANAGER,
            RoleConstants.TEST_USER,RoleConstants.TEST_VIEWER}, logical = Logical.OR)
    public Pager<List<User>> getMemberList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getMemberList(request));
    }

    /**
     * 获取工作空间成员用户 不分页
     */
    @PostMapping("/ws/member/list/all")
    @RequiresRoles(value = {RoleConstants.ORG_ADMIN,RoleConstants.TEST_MANAGER,
            RoleConstants.TEST_USER,RoleConstants.TEST_VIEWER}, logical = Logical.OR)
    public List<User> getMemberList(@RequestBody QueryMemberRequest request) {
        return userService.getMemberList(request);
    }

    /**
     * 添加工作空间成员
     */
    @PostMapping("/ws/member/add")
    @RequiresRoles(value = {RoleConstants.TEST_MANAGER,RoleConstants.ORG_ADMIN}, logical = Logical.OR)
    public void addMember(@RequestBody AddMemberRequest request) {
        String wsId = request.getWorkspaceId();
        workspaceService.checkWorkspaceOwner(wsId);
        userService.addMember(request);
    }

    /**
     * 删除工作空间成员
     */
    @GetMapping("/ws/member/delete/{workspaceId}/{userId}")
    @RequiresRoles(value = {RoleConstants.TEST_MANAGER,RoleConstants.ORG_ADMIN}, logical = Logical.OR)
    public void deleteMember(@PathVariable String workspaceId, @PathVariable String userId) {
        workspaceService.checkWorkspaceOwner(workspaceId);
        userService.deleteMember(workspaceId, userId);
    }

    /**
     * 添加组织成员
     */
    @PostMapping("/org/member/add")
    @RequiresRoles(RoleConstants.ORG_ADMIN)
    public void addOrganizationMember(@RequestBody AddOrgMemberRequest request) {
        organizationService.checkOrgOwner(request.getOrganizationId());
        userService.addOrganizationMember(request);
    }

    /**
     * 删除组织成员
     */
    @GetMapping("/org/member/delete/{organizationId}/{userId}")
    @RequiresRoles(RoleConstants.ORG_ADMIN)
    public void delOrganizationMember(@PathVariable String organizationId, @PathVariable String userId) {
        organizationService.checkOrgOwner(organizationId);
        userService.delOrganizationMember(organizationId, userId);
    }

    /**
     * 查询组织成员列表
     */
    @PostMapping("/org/member/list/{goPage}/{pageSize}")
    @RequiresRoles(value = {RoleConstants.ORG_ADMIN,RoleConstants.TEST_MANAGER}, logical = Logical.OR)
    public Pager<List<User>> getOrgMemberList(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryOrgMemberRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.getOrgMemberList(request));
    }

    /**
     * 组织成员列表不分页
     */
    @PostMapping("/org/member/list/all")
    @RequiresRoles(value = {RoleConstants.ORG_ADMIN,RoleConstants.TEST_MANAGER}, logical = Logical.OR)
    public List<User> getOrgMemberList(@RequestBody QueryOrgMemberRequest request) {
        return userService.getOrgMemberList(request);
    }

    @GetMapping("/besideorg/list/{orgId}")
    public List<User> getBesideOrgMemberList(@PathVariable String orgId) {
        return userService.getBesideOrgMemberList(orgId);
    }

}
