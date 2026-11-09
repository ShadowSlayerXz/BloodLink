package com.bloodlink.ui;

import com.bloodlink.model.BloodRequest;
import com.bloodlink.model.RequestStatus;
import com.bloodlink.model.User;
import com.bloodlink.service.RequestService;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Request table. Admin mode lists all requests and allows status changes; user mode lists own requests. */
public class RequestsPanel extends JPanel {
    private final RequestService service = new RequestService();
    private final User user;
    private final boolean adminMode;
    private final DefaultTableModel model = UiKit.model("ID", "Patient", "Group", "Units", "Hospital", "City",
            "Contact", "Urgency", "Status", "Created");
    private final JTable table = UiKit.table(model);

    public RequestsPanel(User user, boolean adminMode) {
        super(new BorderLayout());
        this.user = user;
        this.adminMode = adminMode;
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> reload());
        JPanel bar = UiKit.flow(refresh);
        if (adminMode) {
            JComboBox<RequestStatus> next = new JComboBox<>(RequestStatus.values());
            JButton apply = new JButton("Set status");
            apply.addActionListener(e -> changeStatus((RequestStatus) next.getSelectedItem()));
            JButton del = new JButton("Delete");
            del.addActionListener(e -> delete());
            bar.add(next); bar.add(apply); bar.add(del);
        } else {
            JButton cancel = new JButton("Cancel selected request");
            cancel.addActionListener(e -> changeStatus(RequestStatus.CANCELLED));
            bar.add(cancel);
        }
        add(bar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        reload();
    }

    public void reload() {
        Async.run(this, () -> adminMode ? service.allRequests(user) : service.myRequests(user), this::fill);
    }

    private void fill(List<BloodRequest> list) {
        model.setRowCount(0);
        for (BloodRequest r : list)
            model.addRow(new Object[]{r.getRequestId(), r.getPatientName(), r.getBloodGroup(), r.getUnitsRequired(),
                    r.getHospitalName(), r.getCity(), r.getContactPhone(), r.getUrgency(), r.getStatus(),
                    r.getCreatedAt() == null ? "" : r.getCreatedAt().toString().replace('T', ' ')});
    }

    private Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) { Async.error(this, "Select a request first."); return null; }
        return (Integer) model.getValueAt(table.convertRowIndexToModel(row), 0);
    }

    private void changeStatus(RequestStatus next) {
        Integer id = selectedId();
        if (id == null) return;
        Async.run(this, () -> { service.updateStatus(user, id, next); return Boolean.TRUE; }, ok -> reload());
    }

    private void delete() {
        Integer id = selectedId();
        if (id == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete request #" + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Async.run(this, () -> { service.delete(user, id); return Boolean.TRUE; }, ok -> reload());
    }
}
