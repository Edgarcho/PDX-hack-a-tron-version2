package dao;

import models.Team;
import models.Member;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Sql2oTeamDaoTest {

    private Sql2oTeamDao teamDao;
    private Sql2oMemberDao memberDao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        teamDao = new Sql2oTeamDao(sql2o);
        memberDao = new Sql2oMemberDao(sql2o);

        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCourseSetsId() throws Exception {
        Team team = new Team("A Team","Group of one");
        int originalTeamId = team.getId();
        teamDao.add(team);
        assertNotEquals(originalTeamId, team.getId());
    }

    @Test
    public void existingTeamsCanBeFoundById() throws Exception {
        Team team  = new Team("A Team", "Group of one");
        teamDao.add(team);
        Team foundTeam = teamDao.findById(team.getId());
        assertEquals(team, foundTeam);
    }

    @Test
    public void addedTeamsAreReturnedFromgetAll() throws Exception{
        Team team = new Team("A Team","Group of one");
        teamDao.add(team);
        assertEquals(1,teamDao.getAll().size());
    }

    @Test
    public void noTeamsReturnsEmptyList() throws Exception {
        assertEquals(0, teamDao.getAll().size());
    }

    @Test
    public void updateChangesTeamName() throws Exception{
        String initialName = "A Team";
        Team team = new Team(initialName,"Group of one");
        teamDao.add(team);
        teamDao.update(team.getId(),"B Team");
        Team updateTeam = teamDao.findById(team.getId());
        assertNotEquals(initialName,updateTeam.getName());
    }

    @Test
    public void updateChangesTeamDescription() throws Exception {
        String initialDescription = "Group of one";
        Team team = new Team("A Team", initialDescription);
        teamDao.add(team);
        teamDao.updateDescription(team.getId(),"Group of Two");
        Team updateTeam = teamDao.findById(team.getId());
        assertNotEquals(initialDescription,updateTeam.getDescription());
    }

    @Test
    public void deleteByIdDeletesCorrectTeam() throws Exception {
        Team team = new Team("A team","Group of one");
        teamDao.add(team);
        teamDao.deleteById(team.getId());
        assertEquals(0,teamDao.getAll().size());
    }

    @Test
    public void clearAllClearsAll() throws Exception {
        Team team = new Team("A Team","Group of one");
        Team otherTeam = new Team("B Team","Group of two");
        teamDao.add(team);
        teamDao.add(otherTeam);
        int daoSize = teamDao.getAll().size();
        teamDao.clearAllTeams();
        assertTrue(daoSize > 0 && daoSize > teamDao.getAll().size());
    }

    @Test
    public void getAllMembersByTeamReturnsMembersCorrectly() throws Exception {
        Team team = new Team("A Team","Group of one");
        teamDao.add(team);
        int teamId = team.getId();
        Member member = new Member("Edgar", teamId);
        Member otherMember = new Member("Bob", teamId);
        Member thirdMember = new Member("Ted", teamId);
        memberDao.add(member);
        memberDao.add(otherMember);

        assertTrue(teamDao.getAllMembersByTeam(teamId).size() == 2);
        assertTrue(teamDao.getAllMembersByTeam(teamId).contains(member));
        assertTrue(teamDao.getAllMembersByTeam(teamId).contains(otherMember));
        assertFalse(teamDao.getAllMembersByTeam(teamId).contains(thirdMember));
    }
}