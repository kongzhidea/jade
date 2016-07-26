package net.paoding.rose.jade.dao;


import net.paoding.rose.jade.annotation.*;
import net.paoding.rose.jade.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DAO
public interface UserDAO {

    String FIELD = " id,username,realname,privs,ctime,city_name ";

    @SQL("select realname from user where id=:id")
    String getRealName(@SQLParam("id") int id);

    @SQL("select $FIELD from user where id=:1")
    User getUser(int id);

    @SQL("select id,realname from user where id in(:1)")
    Map<Integer, String> getUserRealNameMap(List<Integer> ids);

    @SQL("select id,realname from user where id in(:1)")
    @KeyColumnOfMap("realname")
    Map<String, Integer> getUserRealNameMapRev(List<Integer> ids);

    @SQL("select count(id) from user")
    int getTotalUserCount();

    @SQL("select $FIELD from user where 1=1 " +
            " #if(:user.id != 0) {and id=:user.id } " +
            " #if(:user.username != null and :user.username != '') {and username=:user.username } " +
            " #if(:user.realname != null and :user.realname != '') {and realname=:user.realname } ")
    List<User> getUserList(@SQLParam("user") User user);

    @SQL("select $FIELD from user where 1=1 " +
            " #if(:user.id != 0) {and id=:user.id } " +
            " #if(:user.username != null and :user.username != '') {and username=:user.username } " +
            " #if(:user.realname != null and :user.realname != '') {and realname=:user.realname } ")
    User[] getUserArray(@SQLParam("user") User user);

    @SQL("select $FIELD from user where 1=1 " +
            " #if(:user.id != 0) {and id=:user.id } " +
            " #if(:user.username != null and :user.username != '') {and username=:user.username } " +
            " #if(:user.realname != null and :user.realname != '') {and realname=:user.realname } ")
    List<Map<String, Object>> getUserListMap(@SQLParam("user") User user);

    @SQL("select $FIELD from user where 1=1 " +
            " #if(:user.id != 0) {and id=:user.id } " +
            " #if(:user.username != null and :user.username != '') {and username=:user.username } " +
            " #if(:user.realname != null and :user.realname != '') {and realname=:user.realname } ")
    Set<User> getUserSet(@SQLParam("user") User user);

    @SQL("select id from user where 1=1 " +
            " #if(:user.id != 0) {and id=:user.id } " +
            " #if(:user.username != null and :user.username != '') {and username=:user.username } " +
            " #if(:user.realname != null and :user.realname != '') {and realname=:user.realname } ")
    List<Integer> getUserIdList(@SQLParam("user") User user);

    @SQL("select $FIELD from user where realname like :realname")
    List<User> getUserByRealName(@SQLParam("realname") String realname);

    @ReturnGeneratedKeys
    @SQL("insert into user(username,realname,privs,ctime,city_name) values" +
            "(:user.username,:user.realname,:user.privs,:user.ctime,:user.cityName) ")
    int addUser(@SQLParam("user") User user);


    @SQL("update user set username=:user.username,realname=:user.realname,privs=:user.privs,ctime=:user.ctime,city_name=:user.cityName where id=:user.id")
    int updateUser(@SQLParam("user") User user);

    @SQL("delete from user where id = :1")
    int deleteUser(int id);

    @SQL("select realname from eby_user where id= ##(:id) ")
    void testSQL(int id);
}
