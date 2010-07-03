package com.jsonengine.http;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.jsonengine.meta.JEDocMeta;

/**
 * Handles Task Queue tasks.
 * 
 * @author @kazunori_279
 */
public class TQServlet extends HttpServlet {

    private static final String QUENAME_JETASKS = "jetasks";

    public static final String PATH_DELETEALL = "/_tq/deleteAll";

    public static final String PARAM_DOCTYPE = "docType";

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // check URI and invoke a task
        if (req.getRequestURI().startsWith(PATH_DELETEALL)) {
            deleteAll(req);
        }
    }

    // Delete all the entities for a docType
    private void deleteAll(HttpServletRequest req) {

        // find 500 entities for the docType
        final String docType = req.getParameter(PARAM_DOCTYPE);
        final JEDocMeta jdm = new JEDocMeta();
        final List<Key> keys =
            Datastore
                .query(jdm)
                .filter(jdm.docType.equal(docType))
                .limit(500)
                .asKeyList();

        // if there's no entities for the docType, finish the task
        if (keys.isEmpty()) {
            return;
        }

        // delete them all
        Datastore.delete(keys);

        // put another task for deletion
        addDeleteAllTask(docType);
    }

    /**
     * Adds a task on the queue to delete all the entities for a docType.
     * 
     * @param docType
     *            docType to delete
     */
    public static void addDeleteAllTask(String docType) {
        final Queue que = QueueFactory.getQueue(QUENAME_JETASKS);
        final TaskOptions to =
            TaskOptions.Builder.url(PATH_DELETEALL).param(
                PARAM_DOCTYPE,
                docType);
        que.add(to);
    }
}
