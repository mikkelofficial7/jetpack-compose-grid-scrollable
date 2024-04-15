package com.example.jetpackscrollable.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

typealias PhotoResponse = List<Photos>

@Parcelize
data class Photos(
    val id: String,
    val slug: String,
    @SerializedName("alternative_slugs")
    val alternativeSlugs: AlternativeSlugs,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("promoted_at")
    val promotedAt: String,
    val width: Long,
    val height: Long,
    val color: String,
    @SerializedName("blur_hash")
    val blurHash: String,
    val description: String?,
    @SerializedName("alt_description")
    val altDescription: String,
    val breadcrumbs: @RawValue List<Any?>,
    val urls: Urls,
    val links: Links,
    val likes: Long,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean,
    @SerializedName("current_user_collections")
    val currentUserCollections: @RawValue List<Any?>,
    val sponsorship: @RawValue Any?,
    @SerializedName("topic_submissions")
    val topicSubmissions: TopicSubmissions,
    @SerializedName("asset_type")
    val assetType: String,
    val user: User,
) : Parcelable

@Parcelize
data class AlternativeSlugs(
    val en: String,
    val es: String,
    val ja: String,
    val fr: String,
    val it: String,
    val ko: String,
    val de: String,
    val pt: String,
) : Parcelable

@Parcelize
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
    @SerializedName("small_s3")
    val smallS3: String,
) : Parcelable

@Parcelize
data class Links(
    val self: String,
    val html: String,
    val download: String,
    @SerializedName("download_location")
    val downloadLocation: String,
) : Parcelable

@Parcelize
data class TopicSubmissions(
    val people: People?,
    val nature: Nature?,
    val wallpapers: Wallpapers?,
    val spirituality: Spirituality?,
) : Parcelable

@Parcelize
data class People(
    val status: String,
) : Parcelable

@Parcelize
data class Nature(
    val status: String,
) : Parcelable

@Parcelize
data class Wallpapers(
    val status: String,
) : Parcelable

@Parcelize
data class Spirituality(
    val status: String,
) : Parcelable

@Parcelize
data class User(
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val username: String,
    val name: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    val bio: String,
    val location: String,
    val links: Links2,
    @SerializedName("profile_image")
    val profileImage: ProfileImage,
    @SerializedName("instagram_username")
    val instagramUsername: String,
    @SerializedName("total_collections")
    val totalCollections: Long,
    @SerializedName("total_likes")
    val totalLikes: Long,
    @SerializedName("total_photos")
    val totalPhotos: Long,
    @SerializedName("total_promoted_photos")
    val totalPromotedPhotos: Long,
    @SerializedName("total_illustrations")
    val totalIllustrations: Long,
    @SerializedName("total_promoted_illustrations")
    val totalPromotedIllustrations: Long,
    @SerializedName("accepted_tos")
    val acceptedTos: Boolean,
    @SerializedName("for_hire")
    val forHire: Boolean,
    val social: Social,
) : Parcelable

@Parcelize
data class Links2(
    val self: String,
    val html: String,
    val photos: String,
    val likes: String,
    val portfolio: String,
    val following: String,
    val followers: String,
) : Parcelable

@Parcelize
data class ProfileImage(
    val small: String,
    val medium: String,
    val large: String,
) : Parcelable

@Parcelize
data class Social(
    @SerializedName("instagram_username")
    val instagramUsername: String,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("paypal_email")
    val paypalEmail: @RawValue Any?,
) : Parcelable
