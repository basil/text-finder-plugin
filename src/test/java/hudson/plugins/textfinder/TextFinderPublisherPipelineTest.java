package hudson.plugins.textfinder;

import hudson.model.Result;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;

public class TextFinderPublisherPipelineTest {

    private static final String UNIQUE_TEXT = "foobar";
    private static final String fileSet = "out.txt";

    @Rule public JenkinsRule rule = new JenkinsRule();

    @Test
    public void successIfFoundInFile() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  writeFile file: '"
                                + fileSet
                                + "', text: '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', fileSet: '"
                                + fileSet
                                + "', succeedIfFound: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertSuccess(project);
        rule.assertLogContains(
                "[Text Finder] Looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the files at "
                        + "'"
                        + fileSet
                        + "'",
                build);
        TestUtils.assertFileContainsMatch(
                new File(TestUtils.getWorkspace(build), fileSet), UNIQUE_TEXT, rule, build);
    }

    @Test
    public void failureIfFoundInFile() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  writeFile file: '"
                                + fileSet
                                + "', text: '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', fileSet: '"
                                + fileSet
                                + "'\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.FAILURE, project);
        rule.assertLogContains(
                "[Text Finder] Looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the files at "
                        + "'"
                        + fileSet
                        + "'",
                build);
        TestUtils.assertFileContainsMatch(
                new File(TestUtils.getWorkspace(build), fileSet), UNIQUE_TEXT, rule, build);
    }

    @Test
    public void unstableIfFoundInFile() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  writeFile file: '"
                                + fileSet
                                + "', text: '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', fileSet: '"
                                + fileSet
                                + "', unstableIfFound: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.UNSTABLE, project);
        rule.assertLogContains(
                "[Text Finder] Looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the files at "
                        + "'"
                        + fileSet
                        + "'",
                build);
        TestUtils.assertFileContainsMatch(
                new File(TestUtils.getWorkspace(build), fileSet), UNIQUE_TEXT, rule, build);
    }

    @Test
    public void notBuiltIfFoundInFile() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  writeFile file: '"
                                + fileSet
                                + "', text: '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', fileSet: '"
                                + fileSet
                                + "', notBuiltIfFound: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.NOT_BUILT, project);
        rule.assertLogContains(
                "[Text Finder] Looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the files at "
                        + "'"
                        + fileSet
                        + "'",
                build);
        TestUtils.assertFileContainsMatch(
                new File(TestUtils.getWorkspace(build), fileSet), UNIQUE_TEXT, rule, build);
    }

    @Test
    public void notFoundInFile() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  writeFile file: '"
                                + fileSet
                                + "', text: 'foobaz'\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', fileSet: '"
                                + fileSet
                                + "'\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertSuccess(project);
        rule.assertLogContains(
                "[Text Finder] Looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the files at "
                        + "'"
                        + fileSet
                        + "'",
                build);
    }

    @Test
    public void successIfFoundInConsole() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "  testEcho '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "node {\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', succeedIfFound: true, alsoCheckConsoleOutput: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertSuccess(project);
        rule.assertLogContains(TestUtils.PREFIX + UNIQUE_TEXT, build);
        rule.assertLogContains("[Text Finder] Scanning console output...", build);
        rule.assertLogContains(
                "[Text Finder] Finished looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the console output",
                build);
    }

    @Test
    public void failureIfFoundInConsole() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "  testEcho '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "node {\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', alsoCheckConsoleOutput: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.FAILURE, project);
        rule.assertLogContains(TestUtils.PREFIX + UNIQUE_TEXT, build);
        rule.assertLogContains("[Text Finder] Scanning console output...", build);
        rule.assertLogContains(
                "[Text Finder] Finished looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the console output",
                build);
    }

    @Test
    public void unstableIfFoundInConsole() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "  testEcho '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "node {\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', unstableIfFound: true, alsoCheckConsoleOutput: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.UNSTABLE, project);
        rule.assertLogContains(TestUtils.PREFIX + UNIQUE_TEXT, build);
        rule.assertLogContains("[Text Finder] Scanning console output...", build);
        rule.assertLogContains(
                "[Text Finder] Finished looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the console output",
                build);
    }

    @Test
    public void notBuiltIfFoundInConsole() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "  testEcho '"
                                + UNIQUE_TEXT
                                + "'\n"
                                + "node {\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', notBuiltIfFound: true, alsoCheckConsoleOutput: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertStatus(Result.NOT_BUILT, project);
        rule.assertLogContains(TestUtils.PREFIX + UNIQUE_TEXT, build);
        rule.assertLogContains("[Text Finder] Scanning console output...", build);
        rule.assertLogContains(
                "[Text Finder] Finished looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the console output",
                build);
    }

    @Test
    public void notFoundInConsole() throws Exception {
        WorkflowJob project = rule.createProject(WorkflowJob.class);
        project.setDefinition(
                new CpsFlowDefinition(
                        "node {\n"
                                + "  findText regexp: '"
                                + UNIQUE_TEXT
                                + "', alsoCheckConsoleOutput: true\n"
                                + "}\n",
                        true));
        WorkflowRun build = rule.buildAndAssertSuccess(project);
        rule.assertLogContains("[Text Finder] Scanning console output...", build);
        rule.assertLogContains(
                "[Text Finder] Finished looking for pattern '"
                        + UNIQUE_TEXT
                        + "' in the console output",
                build);
    }
}
