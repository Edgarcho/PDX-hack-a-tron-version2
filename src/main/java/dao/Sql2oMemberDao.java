package dao;

import models.Member;
import models.Team;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oMemberDao implements MemberDao {
    private final Sql2o sql2o;

    public Sql2oMemberDao(Sql2o sql2o){
        this.sql2o = sql2o;
    }

    @Override
    public void add(Member member) {
        String sql = "INSERT INTO members (name, teamId) VALUES (:name, :teamId)";
        try (Connection con = sql2o.open()){
            int id = (int) con.createQuery(sql)
                    .addParameter("name", member.getName())
                    .addParameter("teamId", member.getTeamId())
                    .addColumnMapping("NAME","name")
                    .addColumnMapping("TEAMID","teamId")
                    .executeUpdate()
                    .getKey();
            member.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }
}
