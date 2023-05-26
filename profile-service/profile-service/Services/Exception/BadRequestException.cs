namespace profile_service.Services.Exception;

public class BadRequestException : System.Exception
{
    public BadRequestException(string message) : base(message)
    {
    }
}
