package net.paoding.rose.jade.dao;


import net.paoding.rose.jade.annotation.*;
import net.paoding.rose.jade.model.Blog;
import net.paoding.rose.jade.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DAO(catalog = "catalog_blog")
public interface BlogDAO {

    String FIELD = " id,user_id,content,ctime ";

    @SQL("select $FIELD from blog where id = :1 and user_id=:2")
    Blog getBlog(int id, @ShardBy int userId);

    @SQL("insert into blog(user_id,content,ctime) values(:blog.userId,:blog.content,:blog.ctime)")
    @ReturnGeneratedKeys
    int addBlog(@ShardBy int userId, @SQLParam("blog") Blog blog);

    @SQL("select $FIELD from blog where user_id=:1")
    List<Blog> getBlogByUserId(@ShardBy int userId);
}
