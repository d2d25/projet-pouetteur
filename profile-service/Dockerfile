FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 3334
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["profile-service/profile-service.csproj", "profile-service/"]
RUN dotnet restore "profile-service/profile-service.csproj"
COPY . .
WORKDIR "/src/profile-service"
RUN dotnet build "profile-service.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "profile-service.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "profile-service.dll"]

COPY access-refresh-token-keys/* /app/access-refresh-token-keys/
