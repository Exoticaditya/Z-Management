<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registration Confirmation</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #3498db; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background: #f9f9f9; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
        .button { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Welcome to ${organizationName}!</h1>
        </div>
        
        <div class="content">
            <h2>Hello ${user.fullName},</h2>
            
            <p>Thank you for registering with ${organizationName}. Your registration has been received and is currently pending approval.</p>
            
            <h3>Registration Details:</h3>
            <ul>
                <li><strong>Name:</strong> ${user.fullName}</li>
                <li><strong>Email:</strong> ${user.email}</li>
                <li><strong>Department:</strong> ${user.department}</li>
                <li><strong>User Type:</strong> ${user.userType}</li>
                <li><strong>Registration Date:</strong> ${user.createdAt?string("MMM dd, yyyy")}</li>
            </ul>
            
            <p>Our admin team will review your registration and you will receive an email notification once your account is approved.</p>
            
            <p>If you have any questions, please don't hesitate to contact our support team.</p>
            
            <p>Best regards,<br>
            The ${organizationName} Team</p>
        </div>
        
        <div class="footer">
            <p>This is an automated message. Please do not reply to this email.</p>
            <p>&copy; 2024 ${organizationName}. All rights reserved.</p>
        </div>
    </div>
</body>
</html> 