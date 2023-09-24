package com.avocado.expensescompose.di

import com.apollographql.apollo3.ApolloClient
import com.avocado.expensescompose.data.ApolloExpenseClient
import com.avocado.expensescompose.domain.ExpensesClient
import com.avocado.expensescompose.domain.GetExpensesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("http://10.0.2.2:4000/graphql")
            // TODO implement logic to extract this from login
            .addHttpHeader(
                name = "X-Session-Key",
                value = "eyJraWQiOiJpZGhKdk8zS1VKTnh2WjlQN2kwdkI0Z1JxbytSdDNqdEhGdytHUTJ3N1ZzPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIzMTY4ZjVmMC1hYzM3LTRkYzQtYmM3OS0wYjE2ZjM2YTVkYWQiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0yLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMl9SODk0QmxNcHEiLCJjbGllbnRfaWQiOiIyNW9rc2dqbmwyNTg2MzlyNGN2cDRubDB2MiIsIm9yaWdpbl9qdGkiOiIwMDVjZGUyMS01ODNjLTQ1NTItYWMyOS05ODA2ZjQ0ZjNkYTkiLCJldmVudF9pZCI6ImQxMTE3MTk5LWU3NDUtNDIzZi1hYTMzLWE5MzQ1MzdmYTAyNiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE2OTU1OTg0MjgsImV4cCI6MTY5NTYwMjAyOCwiaWF0IjoxNjk1NTk4NDI4LCJqdGkiOiJhZTE3NWQ4MS01ZWUyLTQwNmUtYmZlNi05YTc4MWQzZmM1N2MiLCJ1c2VybmFtZSI6IjMxNjhmNWYwLWFjMzctNGRjNC1iYzc5LTBiMTZmMzZhNWRhZCJ9.pkDuUmdvHQt5leVVpaBcXEWfNkFa1aZ-ys7qcV0F7GmdO54xfQ6HGNWUumqrwIPgXWsfPh1oZiURHBVIH2X-roBLelcY-1W9McfRSfBSsnP3ojEiHLt7u527aiBiisrFUEkGaF_kIVphf2w791JjasTuAgZMSKXRUgi_q_kPtzfBz2oXaKfYCWSNtRXNUYgp5IaEV4U3CQ1doeR5riazd0MfnQnp1hSXH4aonD6q5d0qQUULzwuUV47cPtHOjz6holn-RokU7oxSLty0QUqkKT1GfmWijASqDw1ogajsR6oDiOMWt37VpZqgkcV6YxefiM_c3IDbKNr42NKoJ4V-6w"
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideExpenseClient(apolloClient: ApolloClient): ExpensesClient {
        return ApolloExpenseClient(apolloClient)
    }

    @Provides
    @Singleton
    fun provideExpenseUseCase(expensesClient: ExpensesClient): GetExpensesUseCase {
        return GetExpensesUseCase(expensesClient)
    }
}