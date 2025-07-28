# 🔧 How to Fix Domain, DNS, and File Permission Issues

## 1. 🌐 Domain Pointing to Hosting

### Check if Domain Points to Hosting:

#### Method A: GoDaddy Dashboard
1. **Login to GoDaddy**
2. **My Products → Domains**
3. **zpluse.com → Manage**
4. **Click "DNS" tab**
5. **Look for A Record:**
   - **Type**: A
   - **Name**: @ (or blank)
   - **Points to**: Should be your hosting server IP

#### Method B: Check Hosting Assignment
1. **My Products → Web Hosting**
2. **Click "Manage" on your hosting**
3. **Look for "Primary Domain" or "Assigned Domains"**
4. **Should show**: zpluse.com

### Fix Domain Pointing:

#### Auto-Fix (Easiest):
1. **In hosting control panel**
2. **Look for "Addon Domains" or "Domain Management"**
3. **Add zpluse.com as primary domain**
4. **GoDaddy auto-configures DNS**

#### Manual Fix:
1. **Get hosting server IP** from cPanel → Server Information
2. **Go to Domain DNS settings**
3. **Update A Record** to point to hosting IP

## 2. ⏰ DNS Propagation

### What is DNS Propagation?
- **Time it takes** for domain changes to spread worldwide
- **Can take**: 1-48 hours
- **Usually**: 2-8 hours

### Check DNS Propagation Status:

#### Method A: Online Tools
1. **Visit**: [whatsmydns.net](https://whatsmydns.net)
2. **Enter**: zpluse.com
3. **Select**: A record
4. **Check**: If it shows your hosting IP globally

#### Method B: Command Line
1. **Open Command Prompt**
2. **Type**: `nslookup zpluse.com`
3. **Should return**: Your hosting server IP

#### Method C: Browser Test
1. **Try different browsers**
2. **Use incognito/private mode**
3. **Clear browser cache** (Ctrl+F5)
4. **Try from different devices/networks**

### Speed Up DNS Propagation:
- **Use DNS flush**: `ipconfig /flushdns` (Windows)
- **Change DNS servers**: Use 8.8.8.8, 1.1.1.1
- **Wait it out**: Usually 2-8 hours

## 3. 🔒 File Permissions

### What are File Permissions?
- **Controls who can read/write** your files
- **Wrong permissions** = website won't load
- **Correct permissions**:
  - **Files**: 644 (read/write for owner, read for others)
  - **Folders**: 755 (read/write/execute for owner, read/execute for others)

### Check File Permissions in cPanel:

#### Step 1: Access File Manager
1. **cPanel → File Manager**
2. **Go to public_html**
3. **Right-click on any file**
4. **Select "Change Permissions" or "Permissions"**

#### Step 2: Check Current Permissions
**Should see:**
- **Files (HTML, CSS)**: 644
- **Folders (asset, employee-dashboard)**: 755
- **index.html**: Must be 644

#### Step 3: Fix Wrong Permissions
**If permissions are wrong:**
1. **Select file/folder**
2. **Right-click → Change Permissions**
3. **Set to**:
   - **Files**: 644
   - **Folders**: 755
4. **Apply to all files** if needed

### Quick Permission Fix:

#### For All Files:
1. **Select all files** in public_html
2. **Right-click → Change Permissions**
3. **Set to 644**
4. **Check "Change permissions of all files"**

#### For All Folders:
1. **Select all folders** (asset, employee-dashboard)
2. **Right-click → Change Permissions**
3. **Set to 755**
4. **Check "Change permissions of all subdirectories"**

## 🚀 Quick Diagnostic Steps

### Step 1: Check Domain Pointing (2 minutes)
```bash
# Open Command Prompt and run:
nslookup zpluse.com

# Should return your hosting IP
# If returns parking page IP = not pointing correctly
```

### Step 2: Check File Permissions (1 minute)
1. **cPanel → File Manager → public_html**
2. **Look at index.html permissions**
3. **Should be 644**
4. **If not, fix it**

### Step 3: Test Website Access
```
Try these URLs:
- http://zpluse.com
- https://zpluse.com  
- http://your-hosting-ip/
```

## 🛠️ Common Permission Issues

### Issue: 403 Forbidden Error
**Cause**: Wrong folder permissions
**Fix**: Set folders to 755

### Issue: 500 Internal Server Error
**Cause**: Wrong file permissions or corrupted .htaccess
**Fix**: Set files to 644, check .htaccess

### Issue: Files Not Loading
**Cause**: Files set to 600 or 777
**Fix**: Set to 644

## 📋 Complete Checklist

### Domain Pointing:
- [ ] A record points to hosting IP
- [ ] CNAME for www points to domain
- [ ] No conflicting DNS records
- [ ] Domain assigned to hosting account

### DNS Propagation:
- [ ] nslookup returns hosting IP
- [ ] DNS checker shows propagation
- [ ] Cleared browser cache
- [ ] Waited sufficient time (2-8 hours)

### File Permissions:
- [ ] index.html = 644
- [ ] style.css = 644
- [ ] All HTML files = 644
- [ ] asset folder = 755
- [ ] employee-dashboard folder = 755

## 🚨 Emergency Fixes

### If Domain Won't Point:
1. **Call GoDaddy**: 1-480-463-8387
2. **Say**: "Please point zpluse.com to my hosting account"
3. **They can do it instantly**

### If Permissions Wrong:
1. **Select all files in public_html**
2. **Bulk change to 644**
3. **Set folders to 755**

### If DNS Not Propagating:
1. **Wait 24 hours**
2. **Try different DNS servers**
3. **Contact GoDaddy support**

## 🎯 Most Likely Issues in Order:

1. **Files in wrong location** (zplus-website folder)
2. **Missing index.html** in root
3. **Wrong file permissions** (not 644)
4. **Domain not pointing** to hosting
5. **DNS propagation delay**

**Start with moving files to correct location - that fixes 90% of issues!**
