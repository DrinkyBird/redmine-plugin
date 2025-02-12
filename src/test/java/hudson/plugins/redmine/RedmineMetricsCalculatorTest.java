package hudson.plugins.redmine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Expectations;

import org.junit.Test;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueFactory;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.ProjectFactory;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.bean.VersionFactory;

public class RedmineMetricsCalculatorTest {

  @Test
  public void testCalc() throws MetricsException, RedmineException {
    new Expectations() {
      RedmineManager redmineManager;
      {
        minTimes = 0;

        redmineManager.getProjectManager().getProjects();
        ArrayList<Project> projects = new ArrayList<Project>();
        Project p = ProjectFactory.create(1);
        p.setName("Example");
        projects.add(p);

        redmineManager.getProjectManager().getVersions(p.getId());
        ArrayList<Version> versions = new ArrayList<Version>();
        Version v = VersionFactory.create(1);
        v.setName("v1");
        versions.add(v);

        Map<String, String> params = new HashMap<String, String>();
        params.put("project_id", p.getId().toString());
        params.put("fixed_version_id", "1");
        params.put("status_id", "*");
        redmineManager.getIssueManager().getIssues(params);
        List<Issue> issues = new ArrayList<Issue>();
        Issue issue = IssueFactory.create(1);
        issue.setStatusId(1);
        issue.setSubject("Hello");
        issue.setStatusName("Open");
        issues.add(issue);

        returns(projects, versions, issues);
      }
    };

    RedmineMetricsCalculator rmc = new RedmineMetricsCalculator(
        "http://example.com/", "APIKEY", "Example", "v1", "", "");
    assertEquals(1, rmc.calc().size());
  }

  @Test(expected=MetricsException.class)
  public void testNoSuchProject() throws MetricsException, RedmineException {
    new Expectations() {
      RedmineManager redmineManager;
      {
        minTimes = 0;

        redmineManager.getProjectManager().getProjects();
        ArrayList<Project> projects = new ArrayList<Project>();
        Project p = ProjectFactory.create(1);
        p.setName("Example");
        projects.add(p);
        result = projects;
      }
    };

    RedmineMetricsCalculator rmc = new RedmineMetricsCalculator(
        "http://example.com/", "APIKEY", "NoSuchProject", "v1", "", "");
    rmc.calc();
  }

}
