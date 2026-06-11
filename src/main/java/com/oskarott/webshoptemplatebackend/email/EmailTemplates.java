package com.oskarott.webshoptemplatebackend.email;

import com.oskarott.webshoptemplatebackend.model.Address;
import com.oskarott.webshoptemplatebackend.model.Order;
import com.oskarott.webshoptemplatebackend.model.OrderItem;

import java.math.BigDecimal;

public final class EmailTemplates {

    private EmailTemplates() {}

    public static String welcomeSubject() {
        return "Welcome to our shop!";
    }

    public static String welcomeHtml(String firstName) {
        return baseLayout("Welcome, " + firstName + "!", """
                <h2 style="margin:0 0 16px">Welcome to our shop, %s! 🎉</h2>
                <p style="margin:0 0 12px;color:#444;">
                    Your account has been created successfully. You can now browse our
                    products and place orders.
                </p>
                <p style="margin:0;color:#444;">Happy shopping!</p>
                """.formatted(escapeHtml(firstName)));
    }

    public static String resetPasswordSubject() {
        return "Reset your password";
    }

    public static String resetPasswordHtml(String firstName, String resetLink) {
        return baseLayout("Reset your password", """
                <h2 style="margin:0 0 16px">Reset your password</h2>
                <p style="margin:0 0 16px;color:#444;">
                    Hi %s, we received a request to reset the password for your account.
                    Click the button below to choose a new password. This link expires in 30 minutes.
                </p>
                <p style="margin:0 0 24px;">
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;background:#4f46e5;
                            color:#ffffff;text-decoration:none;border-radius:6px;font-weight:600;">
                    Reset password
                  </a>
                </p>
                <p style="margin:0;color:#888;font-size:13px;">
                    If you did not request a password reset, you can safely ignore this email.
                </p>
                """.formatted(escapeHtml(firstName), escapeHtml(resetLink)));
    }

    public static String orderConfirmationSubject(Long orderId) {
        return "Order confirmation #" + orderId;
    }

    public static String orderConfirmationHtml(Order order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            rows.append("""
                    <tr>
                      <td style="padding:8px 12px;border-bottom:1px solid #eee;">%s</td>
                      <td style="padding:8px 12px;border-bottom:1px solid #eee;text-align:center;">%d</td>
                      <td style="padding:8px 12px;border-bottom:1px solid #eee;text-align:right;">%s NOK</td>
                    </tr>
                    """.formatted(
                    escapeHtml(item.getArticle().getName()),
                    item.getQuantity(),
                    formatAmount(item.getSubtotal())
            ));
        }

        String firstName = order.getUser().getFirstName();

        String body = """
                <h2 style="margin:0 0 16px">Order confirmed! ✅</h2>
                <p style="margin:0 0 16px;color:#444;">
                    Hi %s, thank you for your purchase. Here is a summary of your order:
                </p>

                <table width="100%%" cellpadding="0" cellspacing="0"
                       style="border-collapse:collapse;margin-bottom:16px;">
                  <thead>
                    <tr style="background:#f5f5f5;">
                      <th style="padding:8px 12px;text-align:left;font-weight:600;">Product</th>
                      <th style="padding:8px 12px;text-align:center;font-weight:600;">Qty</th>
                      <th style="padding:8px 12px;text-align:right;font-weight:600;">Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    %s
                  </tbody>
                  <tfoot>
                    <tr>
                      <td colspan="2"
                          style="padding:10px 12px;font-weight:700;text-align:right;">Total</td>
                      <td style="padding:10px 12px;font-weight:700;text-align:right;">
                          %s NOK
                      </td>
                    </tr>
                  </tfoot>
                </table>

                <p style="margin:0 0 8px;color:#444;">
                    <strong>Shipping to:</strong> %s
                </p>
                <p style="margin:0;color:#888;font-size:13px;">Order #%d</p>
                """.formatted(
                escapeHtml(firstName),
                rows,
                formatAmount(order.getTotalPrice()),
                order.getShippingAddress() != null
                        ? escapeHtml(formatAddress(order.getShippingAddress()))
                        : "",
                order.getId()
        );

        return baseLayout("Order #" + order.getId() + " confirmed", body);
    }

    private static String baseLayout(String title, String bodyContent) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width,initial-scale=1"/>
                  <title>%s</title>
                </head>
                <body style="margin:0;padding:0;background:#f9f9f9;font-family:Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f9f9f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:8px;
                                      box-shadow:0 2px 8px rgba(0,0,0,.08);
                                      padding:40px;max-width:600px;">
                          <tr>
                            <td style="padding-bottom:24px;border-bottom:2px solid #4f46e5;">
                              <span style="font-size:20px;font-weight:700;color:#4f46e5;">
                                🛍️ Webshop
                              </span>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding-top:24px;">
                              %s
                            </td>
                          </tr>
                          <tr>
                            <td style="padding-top:32px;border-top:1px solid #eee;
                                        font-size:12px;color:#aaa;text-align:center;">
                              You received this email because you have an account with us.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(escapeHtml(title), bodyContent);
    }

    private static String formatAddress(Address a) {
        StringBuilder sb = new StringBuilder();
        if (a.getFirstName() != null || a.getLastName() != null) {
            sb.append(a.getFirstName() != null ? a.getFirstName() : "")
              .append(" ").append(a.getLastName() != null ? a.getLastName() : "").append(", ");
        }
        if (a.getCompany() != null) sb.append(a.getCompany()).append(", ");
        if (a.getStreet() != null) sb.append(a.getStreet()).append(", ");
        if (a.getAddressLine2() != null) sb.append(a.getAddressLine2()).append(", ");
        if (a.getPostalCode() != null) sb.append(a.getPostalCode()).append(" ");
        if (a.getArea() != null) sb.append(a.getArea()).append(", ");
        if (a.getCountry() != null) sb.append(a.getCountry());
        return sb.toString().replaceAll(",\\s*$", "").trim();
    }

    private static String formatAmount(BigDecimal amount) {
        return String.format("%.2f", amount);
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
