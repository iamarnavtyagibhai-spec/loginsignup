# Cloudinary Setup Guide

## Step 1: Create a Cloudinary Account
1. Go to [https://cloudinary.com/](https://cloudinary.com/)
2. Sign up for a free account
3. After signing up, you'll be redirected to your dashboard

## Step 2: Get Your Cloudinary Credentials
1. In your Cloudinary dashboard, you'll see your **Account Details**
2. Copy the following values:
   - **Cloud Name** (e.g., `dabc123`)
   - **API Key** (e.g., `123456789012345`)
   - **API Secret** (e.g., `abcdefghijklmnopqrstuvwxyz123456`)

## Step 3: Update application.yml
Update the `application.yml` file with your Cloudinary credentials:

```yaml
cloudinary:
  cloud-name: your-cloud-name-here
  api-key: your-api-key-here
  api-secret: your-api-secret-here
```

**Important:** Keep your API Secret secure! Never commit it to public repositories.

## Step 4: How It Works
- When you upload a song, it's automatically uploaded to Cloudinary
- Songs are stored in the `musify/songs` folder in your Cloudinary account
- The `url` field in the Song object contains the direct Cloudinary URL to stream the song
- The mobile app can use this URL directly to play the song

## Step 5: Testing
1. Upload a song using the `/api/songs/upload` endpoint
2. Check the response - the `url` field will be a Cloudinary URL (starts with `https://res.cloudinary.com/`)
3. You can open this URL in a browser to verify the file is accessible
4. Use this URL in your mobile app to stream/play the song

## Benefits of Cloudinary
- ✅ Fast CDN delivery
- ✅ Automatic file optimization
- ✅ Direct URL access (no need for additional endpoints)
- ✅ Scalable storage
- ✅ Free tier available (25 GB storage, 25 GB bandwidth/month)

