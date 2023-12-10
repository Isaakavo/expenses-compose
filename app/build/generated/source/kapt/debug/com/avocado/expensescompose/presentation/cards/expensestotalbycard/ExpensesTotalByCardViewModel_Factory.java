package com.avocado.expensescompose.presentation.cards.expensestotalbycard;

import com.avocado.expensescompose.data.apolloclients.GraphQlClientImpl;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class ExpensesTotalByCardViewModel_Factory implements Factory<ExpensesTotalByCardViewModel> {
  private final Provider<GraphQlClientImpl> graphQlClientImplProvider;

  public ExpensesTotalByCardViewModel_Factory(
      Provider<GraphQlClientImpl> graphQlClientImplProvider) {
    this.graphQlClientImplProvider = graphQlClientImplProvider;
  }

  @Override
  public ExpensesTotalByCardViewModel get() {
    return newInstance(graphQlClientImplProvider.get());
  }

  public static ExpensesTotalByCardViewModel_Factory create(
      Provider<GraphQlClientImpl> graphQlClientImplProvider) {
    return new ExpensesTotalByCardViewModel_Factory(graphQlClientImplProvider);
  }

  public static ExpensesTotalByCardViewModel newInstance(GraphQlClientImpl graphQlClientImpl) {
    return new ExpensesTotalByCardViewModel(graphQlClientImpl);
  }
}
