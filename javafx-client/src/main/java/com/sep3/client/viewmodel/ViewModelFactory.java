package com.sep3.client.viewmodel;

/**
 * Factory for creating and managing ViewModels.
 * Implements the MVVM pattern by providing centralized ViewModel creation.
 */
public class ViewModelFactory {
    
    private LoginViewModel loginViewModel;
    private MainViewModel mainViewModel;
    private ProductListViewModel productListViewModel;
    private ProductDetailViewModel productDetailViewModel;
    private OrderListViewModel orderListViewModel;
    private OrderDetailViewModel orderDetailViewModel;
    private ReportsViewModel reportsViewModel;
    
    public ViewModelFactory() {
        // ViewModels are created lazily
    }
    
    public LoginViewModel getLoginViewModel() {
        if (loginViewModel == null) {
            loginViewModel = new LoginViewModel();
        }
        return loginViewModel;
    }
    
    public MainViewModel getMainViewModel() {
        if (mainViewModel == null) {
            mainViewModel = new MainViewModel();
        }
        return mainViewModel;
    }
    
    public ProductListViewModel getProductListViewModel() {
        if (productListViewModel == null) {
            productListViewModel = new ProductListViewModel();
        }
        return productListViewModel;
    }
    
    public ProductDetailViewModel getProductDetailViewModel() {
        if (productDetailViewModel == null) {
            productDetailViewModel = new ProductDetailViewModel();
        }
        return productDetailViewModel;
    }
    
    public OrderListViewModel getOrderListViewModel() {
        if (orderListViewModel == null) {
            orderListViewModel = new OrderListViewModel();
        }
        return orderListViewModel;
    }
    
    public OrderDetailViewModel getOrderDetailViewModel() {
        if (orderDetailViewModel == null) {
            orderDetailViewModel = new OrderDetailViewModel();
        }
        return orderDetailViewModel;
    }
    
    public ReportsViewModel getReportsViewModel() {
        if (reportsViewModel == null) {
            reportsViewModel = new ReportsViewModel();
        }
        return reportsViewModel;
    }
}
