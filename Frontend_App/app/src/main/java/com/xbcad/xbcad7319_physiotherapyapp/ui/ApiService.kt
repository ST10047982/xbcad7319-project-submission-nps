package com.xbcad.xbcad7319_physiotherapyapp.ui

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

interface ApiService {

    @POST("api/auth/register")
    fun registerPatient(@Body user: User): Call<ResponseBody>

    @POST("api/auth/login")
    fun loginPatient(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/logout")
    fun logoutUser(@Header("Authorization") token: String): Call<Void>

    @POST("api/auth/login")
    fun loginStaff(@Body loginRequest: LoginRequest): Call<ResponseBody>

    @POST("api/auth/forget-password")
    fun updatePassword(@Body request: PasswordUpdateRequest): Call<ResponseBody>

    @POST("api/billing")
    fun submitBillingData(
        @Body billingRequest: BillingRequest
    ): Call<ResponseBody>

    @POST("api/form2/createForm2")
    fun submitForm2Data(
        @Body form2Request: Form2Request
    ): Call<ResponseBody>


    @POST("api/form2/createForm1")
    fun submitForm1Data(
        @Body form1Request: Form1Request
    ): Call<ResponseBody>

    @POST("/api/auth/register")
   fun registerUser(@Body user: User): Call<ResponseBody>

    @POST("/api/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ResponseBody>


    @POST("api/appointments/book")
    fun bookAppointment(
        @Header("Authorization") token: String, // Add the authorization header
        @Body appointmentRequest: BookAppointmentRequest
    ): Call<ResponseBody>

    @PUT("api/appointments/{appointmentId}")
    fun rescheduleAppointment(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String,
        @Body rescheduleRequest: RescheduleAppointmentRequest
    ): Call<Map<String,Any>>

    @DELETE("api/appointments/{appointmentId}")
    fun cancelAppointment(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String
    ): Call<ResponseBody>

    @GET("api/appointments/notifications/patient")
    fun getPatientNotifications(
        @Header("Authorization") token: String
    ): Call<NotificationsResponse>

    @GET("api/appointments/notifications/staff")
    fun getStaffNotifications(
        @Header("Authorization") token: String
    ): Call<NotificationsResponse>



    @GET("api/appointments/myappointments")
    fun getAllConfirmedAppointments(
        @Header("Authorization") token: String
    ): Call<List<AppointmentDetails>>

    @GET("api/appointments/allappointments")
    fun getAllAppointments(
        @Header("Authorization") token: String
    ): Call<List<AppointmentDetails>>

    @GET("api/appointments/notes/{appointmentId}")
    fun getAppointmentNotes(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String
    ): Call<AppointmentNotesResponse>



    data class AppointmentNotesResponse(
        val notes: List<String> // Assuming the response contains a list of notes
    )


    // New approve appointment method
    @PUT("api/appointments/{appointmentId}/approve")
    fun approveAppointment(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String
    ): Call<ResponseBody>

    @PUT("api/appointments/notes/{appointmentId}")
    fun addAppointmentNotes(
        @Header("Authorization") token: String,
        @Path("appointmentId") appointmentId: String,
        @Body requestBody: Map<String, String> // Or use a data class
    ): Call<ResponseBody>


    @GET("api/medicalHistory")
    fun getMedicalHistory(@Header("Authorization") token: String): Call<MedicalHistory>

    @POST("api/medicalHistory")
    fun saveMedicalHistory(@Header("Authorization") token: String, @Body medicalHistory: MedicalHistory): Call<Void>

    // Assuming the response body now returns an object with a 'patientNames' field
    @GET("api/patient/profile/patients/names")
    fun getPatientNames(
        @Header("Authorization") authToken: String
    ): Call<PatientNamesResponse>

    // Data class to map the response
    data class PatientNamesResponse(
        val patientNames: List<String>
    )



    // API call to fetch a specific patient's profile by ID
    @GET("api/patient/profile/patient/{id}")
    fun getPatientProfileById(
        @Path("id") patientID: String
    ): Call<ProfileData>


    @GET("api/patient/profile/patient/profile")
    fun getPatientProfile(
        @Header("Authorization") authToken: String
    ): Call<ProfileData>

    @PUT("api/patient/profile/update")
    fun savePatientProfile(
        @Header("Authorization") authToken: String,
        @Body profileData: ProfileData
    ): Call<Map<String, Any>>

    @GET("api/medical-tests")
    fun getMedicalTests(@Header("Authorization") token: String): Call<List<MedicalTest>>

    @POST("api/medical-tests")
    fun addMedicalTest(
        @Header("Authorization") token: String,
        @Body medicalTest: MedicalTest
    ): Call<ResponseBody>

    @DELETE("api/medical-tests/{testId}")
    fun deleteMedicalTest(
        @Header("Authorization") token: String,
        @Path("testId") testId: String
    ): Call<ResponseBody>

    @GET("api/patient/profile/patients/namestoID")
    fun getUserIdByNameAndSurname(
        @Query("name") name: String,
        @Query("surname") surname: String
    ): Call<UserIdResponse>

}

data class UserIdResponse(
    val userId: String
)

data class ProfileData(
    val email: String,
    val phoneNumber: String,
    val medicalAid: String,
    val medicalAidNumber: String
)

data class Patient(
    val _id: String,
    val surname: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val medicalAid: String,
    val medicalAidNumber: String
)




data class BillingRequest(
    val patientName: String,
    val cost: Double
)


// Request model for booking an appointment
data class BookAppointmentRequest(
    val patient: String,
    val date: String,
    val time: String,
    val description: String
)

data class RescheduleAppointmentRequest(
    val date: Long,  // Timestamp of the selected date
    val time: String,  // Selected time
    val description: String  // Appointment description
)

data class RescheduleAppointmentResponse(
    val message: String,
    val appointment: AppointmentDetails
)


data class AppointmentDetails(
    @SerializedName("_id") val id: String,
    val patient: Patient, // Add a nested Patient object
    val date: String,
    val time: String,
    val description: String?,
    val notes: String?,
    val status: String
)

data class PatientNames(
    val name: String,
    val email: String
)

data class Notification(
    val appointmentId: String,
    val message: String,
    val date: String,
    val time: String,
    val description: String,
    val status: String
)

data class NotificationsResponse(
    val notifications: List<Notification>
)

data class User(
    var username: String,
    var password: String,
    var role: String,
    var name: String,
    var surname: String,
    var email: String,
    var phoneNumber: String,
    var medicalAid: String? = null,
    var medicalAidNumber: String? = null
)


data class LoginRequest(
    var username: String,
    var password: String
)

data class PasswordUpdateRequest(
    val username: String,
    val email: String? = null,
    val newPassword: String
)

data class Form2Request(
    val name: String,
    val areasConcernedForNeedling: String,
    val date: Date,
    val signature: String,
    val form2Filled: Boolean
)


data class Form1Request(
    val firstNameP: String,
    val surnameP: String,
    val titleP: String?,
    val idP: String,
    val ageP: Int,
    val addressP: String,
    val codeP: String,
    val cellNumberP: String,
    val workNumberP: String?,
    val homeNumberP: String?,
    val emailP: String,
    val medicalAidNameP: String? = null,
    val medicalAidNumberP: String? = null,
    val firstNameR: String? = null,
    val surnameR: String? = null,
    val titleR: String? = null,
    val idR: String? = null,
    val ageR: String? = null,
    val addressR: String? = null,
    val codeR: String? = null,
    val cellNumberR: String? = null,
    val workNumberR: String? = null,
    val homeNumberR: String? = null,
    val emailR: String? = null,
    val firstNameK: String,
    val addressK: String,
    val codeK: String,
    val nameS: String,
    val typeS: String,
    val signature: String?, // Assuming signature is a String, change if it's different
    val placeS: String,
    val date: Date, // The formatted date from the CalendarView
    val form1Filled: Boolean
)

// Data class for Medical History
data class MedicalHistory(
    val allergies: String,
    val injuries: String,
    val procedures: String,
    val medications: String,
    val familyHistory: String
)

data class MedicalTest(
    val id: String,
    val patientId: String,
    val testName: String,
    val testDate: String,
    val testResults: String,
    val imageUrl: String?,
    val notes: String?
)