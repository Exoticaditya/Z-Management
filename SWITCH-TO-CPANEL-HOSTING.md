# 🔄 Step-by-Step: Switch to Traditional cPanel Hosting

## Step 1: Purchase cPanel Hosting from GoDaddy

### 1.1 Go to GoDaddy Hosting
1. **Open new tab**: Go to [godaddy.com](https://godaddy.com)
2. **Click "Hosting"** in the top menu
3. **Select "Web Hosting"** (not Website Builder)

### 1.2 Choose Hosting Plan
1. **Select "Economy"** plan (~$2.99/month)
2. **OR "Deluxe"** if you want unlimited websites
3. **Click "Add to Cart"**

### 1.3 Configure Domain
1. **Select "I'll use my existing domain"**
2. **Enter**: `zpluse.com`
3. **Continue to checkout**

### 1.4 Complete Purchase
1. **Review order** (should be $2.99-5.99/month)
2. **Complete payment**
3. **Wait for setup email** (5-30 minutes)

## Step 2: Access Your New cPanel Hosting

### 2.1 Check Email
1. **Look for email** from GoDaddy with subject like:
   - "Your hosting account is ready"
   - "Welcome to GoDaddy Hosting"
2. **Note the cPanel login details**

### 2.2 Access cPanel
**Method A: Through GoDaddy Dashboard**
1. **Login to GoDaddy**
2. **Go to "My Products"**
3. **Find "Web Hosting"** section
4. **Click "Manage"**
5. **Click "cPanel Admin"**

**Method B: Direct cPanel URL**
1. **Go to**: `https://zpluse.com:2083`
2. **OR**: `https://cpanel.zpluse.com`
3. **Login with credentials from email**

## Step 3: Upload Your Website Files

### 3.1 Open File Manager
1. **In cPanel, find "File Manager"**
2. **Click to open it**
3. **Navigate to "public_html" folder**

### 3.2 Clear Existing Files (if any)
1. **Select all files** in public_html
2. **Delete them** (to remove any default pages)

### 3.3 Upload Your Z+ Management Files
1. **Click "Upload" button**
2. **Upload ALL files from**: `c:\z+\godaddy-upload\`

**Files to upload:**
```
✅ index.html (homepage)
✅ style.css
✅ about.html
✅ contact.html
✅ client.html
✅ All sector pages (.html files)
✅ asset/ folder (images, logos)
✅ employee-dashboard/ folder
```

### 3.4 Extract/Organize Files
1. **If you uploaded a ZIP file, extract it**
2. **Ensure index.html is in public_html root**
3. **Verify asset/ folder is in public_html/asset/**

## Step 4: Configure Domain DNS

### 4.1 Point Domain to New Hosting
1. **Go to GoDaddy → My Products → Domains**
2. **Find zpluse.com → Manage**
3. **Go to DNS tab**
4. **Update A record** to point to new hosting server
   - This should happen automatically
   - If not, GoDaddy support can help

### 4.2 Update Nameservers (if needed)
- Usually automatic when hosting and domain are both with GoDaddy
- If not working, contact GoDaddy support

## Step 5: Test Your Website

### 5.1 Wait for Propagation
- **DNS changes take 1-24 hours**
- **Usually works within 1-2 hours**

### 5.2 Test Your Site
1. **Visit**: `https://zpluse.com`
2. **Check**: All pages load correctly
3. **Verify**: Images and styling work
4. **Test**: Navigation links

## 🚀 Quick Upload Commands

### If you prefer FTP upload:
**FTP Details** (from GoDaddy email):
- **Host**: Usually your domain or server IP
- **Username**: Your cPanel username
- **Password**: Your cPanel password
- **Port**: 21 (FTP) or 22 (SFTP)

### Upload via FTP client:
1. **Download FileZilla** (free FTP client)
2. **Connect using above details**
3. **Upload all files from** `c:\z+\godaddy-upload\`
4. **Place in public_html folder**

## 💰 Cost Breakdown

### Traditional Hosting:
- **Domain**: Already owned ✅
- **Web Hosting**: $2.99-5.99/month
- **SSL Certificate**: Free with hosting ✅
- **Total**: ~$36-72/year

### vs Website Builder:
- **Website Builder**: What you have now
- **Traditional Hosting**: More control, direct file upload

## 📞 Need Help?

### GoDaddy Support:
- **Phone**: 1-480-463-8387
- **Chat**: Available 24/7
- **Say**: "I want to add cPanel hosting to my domain zpluse.com"

### Common Issues:
1. **Can't find cPanel**: Check "Web Hosting" in My Products
2. **Files not showing**: Clear browser cache, wait for DNS
3. **Images broken**: Check file paths and upload asset folder

## ✅ Success Checklist

- [ ] Purchase cPanel hosting
- [ ] Receive setup email
- [ ] Access cPanel/File Manager
- [ ] Upload all files to public_html
- [ ] Test https://zpluse.com
- [ ] Verify all pages work
- [ ] Check mobile responsiveness

**Your Z+ Management Platform will have complete control with traditional hosting!**

## 🎯 Next Steps After Upload

1. **Set up SSL certificate** (usually automatic)
2. **Configure email accounts** (if needed)
3. **Set up backups**
4. **Add Google Analytics**
5. **Submit to search engines**

**Ready to start? Go to godaddy.com → Hosting → Web Hosting and choose the Economy plan!**
