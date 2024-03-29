package org.apache.hadoop.mapred;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.hadoop.http.HtmlQuoting;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.JSPUtil.JobWithViewAccessCheck;
import org.apache.hadoop.util.*;
import java.lang.Integer;
import java.text.SimpleDateFormat;

public final class jobtasks_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

	private static final long serialVersionUID = 1L;

 static SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy HH:mm:ss") ; 
  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.apache.jasper.runtime.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write('\n');
      out.write('\n');
      out.write('\n');

  final JobTracker tracker = (JobTracker) application.getAttribute("job.tracker");
  String trackerName = 
           StringUtils.simpleHostname(tracker.getJobTrackerMachine());
  String jobid = request.getParameter("jobid");
  if (jobid == null) {
    out.println("<h2>Missing 'jobid'!</h2>");
    return;
  }
  final JobID jobidObj = JobID.forName(jobid);

  JobWithViewAccessCheck myJob = JSPUtil.checkAccessAndGetJob(tracker, jobidObj,
      request, response);
  if (!myJob.isViewJobAllowed()) {
    return; // user is not authorized to view this job
  }

  JobInProgress job = myJob.getJob();

  String type = request.getParameter("type");
  String pagenum = request.getParameter("pagenum");
  String state = request.getParameter("state");
  state = (state!=null) ? state : "all";
  int pnum = Integer.parseInt(pagenum);
  int next_page = pnum+1;
  int numperpage = 2000;
  TaskReport[] reports = null;
  int start_index = (pnum - 1) * numperpage;
  int end_index = start_index + numperpage;
  int report_len = 0;
  if ("map".equals(type)) {
    reports = (job != null) ? tracker.getMapTaskReports(jobidObj) : null;
  } else if ("reduce".equals(type)) {
    reports = (job != null) ? tracker.getReduceTaskReports(jobidObj) : null;
  } else if ("cleanup".equals(type)) {
    reports = (job != null) ? tracker.getCleanupTaskReports(jobidObj) : null;
  } else if ("setup".equals(type)) {
    reports = (job != null) ? tracker.getSetupTaskReports(jobidObj) : null;
  }

      out.write("\n\n<!DOCTYPE html>\n<html>\n  <head>\n    <title>Hadoop ");
      out.print(type);
      out.write(" task list for ");
      out.print(jobid);
      out.write(" on ");
      out.print(trackerName);
      out.write("</title>\n    <link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hadoop.css\">\n  </head>\n<body>\n<h1>Hadoop ");
      out.print(type);
      out.write(" task list for \n<a href=\"jobdetails.jsp?jobid=");
      out.print(jobid);
      out.write('"');
      out.write('>');
      out.print(jobid);
      out.write("</a> on \n<a href=\"jobtracker.jsp\">");
      out.print(trackerName);
      out.write("</a></h1>\n");

    if (job == null) {
    out.print("<b>Job " + jobid + " not found.</b><br>\n");
    return;
  }
  // Filtering the reports if some filter is specified
  if (!"all".equals(state)) {
    List<TaskReport> filteredReports = new ArrayList<TaskReport>();
    for (int i = 0; i < reports.length; ++i) {
      if (("completed".equals(state) && reports[i].getCurrentStatus() == TIPStatus.COMPLETE) 
          || ("running".equals(state) && reports[i].getCurrentStatus() == TIPStatus.RUNNING) 
          || ("killed".equals(state) && reports[i].getCurrentStatus() == TIPStatus.KILLED) 
          || ("pending".equals(state)  && reports[i].getCurrentStatus() == TIPStatus.PENDING)) {
        filteredReports.add(reports[i]);
      }
    }
    // using filtered reports instead of all the reports
    reports = filteredReports.toArray(new TaskReport[0]);
    filteredReports = null;
  }
  report_len = reports.length;
  
  if (report_len <= start_index) {
    out.print("<b>No such tasks</b>");
  } else {
    out.print("<hr>");
    out.print("<h2>" + Character.toUpperCase(state.charAt(0)) 
              + state.substring(1).toLowerCase() + " Tasks</h2>");
    out.print("<center>");
    out.print("<table border=2 cellpadding=\"5\" cellspacing=\"2\">");
    out.print("<tr><td align=\"center\">Task</td><td>Complete</td><td>Status</td>" +
              "<td>Start Time</td><td>Finish Time</td><td>Errors</td><td>Counters</td></tr>");
    if (end_index > report_len){
        end_index = report_len;
    }
    for (int i = start_index ; i < end_index; i++) {
          TaskReport report = reports[i];
          out.print("<tr><td><a href=\"taskdetails.jsp?tipid=" +
            report.getTaskID() + "\">"  + report.getTaskID() + "</a></td>");
         out.print("<td>" + StringUtils.formatPercent(report.getProgress(),2) +
        		   ServletUtil.percentageGraph(report.getProgress() * 100f, 80) + "</td>");
         out.print("<td>"  + HtmlQuoting.quoteHtmlChars(report.getState()) + "<br/></td>");
         out.println("<td>" + StringUtils.getFormattedTimeWithDiff(dateFormat, report.getStartTime(),0) + "<br/></td>");
         out.println("<td>" + StringUtils.getFormattedTimeWithDiff(dateFormat, 
             report.getFinishTime(), report.getStartTime()) + "<br/></td>");
         String[] diagnostics = report.getDiagnostics();
         out.print("<td><pre>");
         for (int j = 0; j < diagnostics.length ; j++) {
             out.println(HtmlQuoting.quoteHtmlChars(diagnostics[j]));
         }
         out.println("</pre><br/></td>");
         out.println("<td>" + 
             "<a href=\"taskstats.jsp?tipid=" + report.getTaskID() +
             "\">" + report.getCounters().size() +
             "</a></td></tr>");
    }
    out.print("</table>");
    out.print("</center>");
  }
  if (end_index < report_len) {
    out.print("<div style=\"text-align:right\">" + 
              "<a href=\"jobtasks.jsp?jobid="+ jobid + "&type=" + type +
              "&pagenum=" + next_page + "&state=" + state +
              "\">" + "Next" + "</a></div>");
  }
  if (start_index != 0) {
      out.print("<div style=\"text-align:right\">" + 
                "<a href=\"jobtasks.jsp?jobid="+ jobid + "&type=" + type +
                "&pagenum=" + (pnum -1) + "&state=" + state + "\">" + "Prev" + "</a></div>");
  }

      out.write("\n\n<hr>\n<a href=\"jobtracker.jsp\">Go back to JobTracker</a><br>\n");

out.println(ServletUtil.htmlFooter());

      out.write('\n');
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
